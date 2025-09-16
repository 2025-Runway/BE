package com.example.runway.domain.auth.controller;

import com.example.runway.domain.auth.dto.LoginResponse;
import com.example.runway.domain.auth.dto.TestLoginRequest;
import com.example.runway.domain.auth.dto.TestLoginResponse;
import com.example.runway.domain.auth.service.AuthService;
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
    public ResponseEntity<?> kakaoLogin(@RequestParam("code") String accessCode) {

        LoginResponse result = authService.oAuthLogin(accessCode);

        log.info("kakao login result: {}", result);

        log.info(result.toString());

        return ResponseEntity.ok(result);
    }

    @PostMapping("/auth/test")
    public ResponseEntity<TestLoginResponse> testLogin(@RequestBody TestLoginRequest request) {
        TestLoginResponse response = authService.testLogin(request.email());
        return ResponseEntity.ok(response);
    }


}