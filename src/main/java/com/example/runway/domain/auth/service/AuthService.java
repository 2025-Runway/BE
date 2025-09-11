package com.example.runway.domain.auth.service;

import com.example.runway.domain.auth.dto.LoginResponse;
import com.example.runway.domain.auth.dto.TestLoginResponse;
import com.example.runway.domain.user.entity.User;
import com.example.runway.domain.user.repository.UserRepository;
import com.example.runway.domain.user.entity.UserStatus;
import com.example.runway.global.jwt.JwtProvider;
import com.example.runway.infra.kakao.KakaoOAuthClient;
import com.example.runway.infra.kakao.dto.KakaoTokenResponseDto;
import com.example.runway.infra.kakao.dto.KakaoUserInfoResponseDto;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final KakaoOAuthClient kakaoOAuthClient;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    @Transactional
    public LoginResponse oAuthLogin(String code, HttpServletResponse response) {
        // 1) 토큰 교환
        KakaoTokenResponseDto token = kakaoOAuthClient.exchangeToken(code);

        // 2) 유저 정보 조회
        KakaoUserInfoResponseDto info = kakaoOAuthClient.getUser(token.getAccessToken());

        // 3) 사용자 upsert (email 없을 수 있어 nickname+id 조합 등으로 키 설계)
        String kakaoId = String.valueOf(info.getId());
        String email = info.getKakaoAccount() != null ? info.getKakaoAccount().getEmail() : null;
        String nickname = (info.getKakaoAccount() != null && info.getKakaoAccount().getProfile()!=null)
                ? info.getKakaoAccount().getProfile().getNickname() : "kakao-user";
        String profileImageUrl = (info.getKakaoAccount() != null && info.getKakaoAccount().getProfile() != null)
                ? info.getKakaoAccount().getProfile().getProfileImageUrl()
                : null;

//        log.info("[Kakao Login] kakaoId={}", kakaoId);
//        log.info("[Kakao Login] email={}", email);
//        log.info("[Kakao Login] nickname={}", nickname);
//        log.info("[Kakao Login] profileImageUrl={}", profileImageUrl);


        //TODO - 4) 유저저장
        User user = userRepository.findByKakaoId(kakaoId)
                .orElseGet(() ->
                        User.builder()
                                .kakaoId(kakaoId)
                                .email(email)
                                .nickname(nickname)
                                .profileImageUrl(profileImageUrl)
                                .status(UserStatus.ACTIVE)
                                .destination("")
                                .build()
                );

        user = userRepository.save(user);

        //TODO 5) 우리 서비스용 JWT 발급
        String userId = user.getId().toString();
        String accessJwt = jwtProvider.createAccessToken(userId, Map.of(
                "nickname", user.getNickname()
        ));

//        addHttpOnlyCookie(response, "ACCESS_TOKEN", accessJwt, 3600);

        return LoginResponse.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .profileImageUrl(user.getProfileImageUrl())
                .accessToken(accessJwt)
                .build();
    }

    @Transactional(readOnly = true)
    public TestLoginResponse testLogin(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자입니다."));

        String accessToken = jwtProvider.createAccessToken(user.getId().toString(), Map.of(
                "nickname", user.getNickname()
        ));

        return new TestLoginResponse(accessToken);
    }
}
