package com.example.runway.domain.auth.util;

import org.springframework.beans.factory.annotation.Value;

public class KakaoUtil {
    @Value("${spring.kakao.auth.client}")
    private String client;
    @Value("${spring.kakao.auth.redirect}")
    private String redirect;
}
