package com.example.runway.domain.auth.controller;

import com.example.runway.domain.auth.dto.LoginResponse;
import com.example.runway.domain.auth.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class AuthController {

    private final AuthService authService;


    @GetMapping("/auth/login/kakao")
    public ResponseEntity<?> kakaoLogin(@RequestParam("code") String accessCode, HttpServletResponse httpServletResponse) {

        LoginResponse result = authService.oAuthLogin(accessCode, httpServletResponse);

        log.info("kakao login result: {}", result);

        log.info(result.toString());

        return ResponseEntity.ok(result);
    }

}