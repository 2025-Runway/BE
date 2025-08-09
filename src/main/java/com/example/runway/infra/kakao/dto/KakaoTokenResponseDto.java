package com.example.runway.infra.kakao.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class KakaoTokenResponseDto {
    @JsonProperty("access_token") private String accessToken;
    @JsonProperty("token_type")   private String tokenType;
    @JsonProperty("refresh_token") private String refreshToken;
    @JsonProperty("expires_in")    private Long expiresIn;
    private String scope;
    @JsonProperty("id_token")      private String idToken; // OIDC 켠 경우
}
