package com.example.runway.infra.kakao.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class KakaoUserInfoResponseDto {
    private Long id;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @Getter @Setter
    public static class KakaoAccount {
        private String email;
        private Profile profile;
    }

    @Getter @Setter
    public static class Profile {
        @JsonProperty("nickname") private String nickname;
        @JsonProperty("profile_image_url") private String profileImageUrl;
    }
}
