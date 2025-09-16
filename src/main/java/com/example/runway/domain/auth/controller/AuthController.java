package com.example.runway.domain.auth.controller;

import com.example.runway.domain.auth.dto.LoginResponse;
import com.example.runway.domain.auth.dto.TestLoginRequest;
import com.example.runway.domain.auth.dto.TestLoginResponse;
import com.example.runway.domain.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class AuthController {

    private final AuthService authService;


    @GetMapping("/auth/login/kakao")
    @Operation(
            summary = "카카오 로그인 API",
            description = "카카오 로그인. 프론트에서 인증코드 받아서 전달. redirectUrl : https://api.run-way.site/auth/login/kakao"
    )
    public ResponseEntity<?> kakaoLogin(
            @Parameter(description = "인가 코드", example = "aBcDeFgHiJkLmNoPqRsTuVwXyZ12345")
            @RequestParam("code") String accessCode) {
        LoginResponse result = authService.oAuthLogin(accessCode);

        log.info("kakao login result: {}", result);

        log.info(result.toString());

        return ResponseEntity.ok(result);
    }

    @PostMapping("/auth/test")
    @Operation(
            summary = "테스트 로그인 API",
            description = "테스트 로그인 emil로만 로그인"
    )
    public ResponseEntity<TestLoginResponse> testLogin(
            @Parameter(description = "유저 이메일", example = "test@run-way.site")
            @RequestBody TestLoginRequest request) {
        TestLoginResponse response = authService.testLogin(request.email());
        return ResponseEntity.ok(response);
    }


}