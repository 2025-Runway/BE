package com.example.runway.domain.auth.dto;

import lombok.Builder;

@Builder
public record LoginResponse(
        Long userId,
        String nickname,
        String email,
        String profileImageUrl,
        String accessToken
) {}
