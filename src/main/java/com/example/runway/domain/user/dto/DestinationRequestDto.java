package com.example.runway.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "여행지 요청 DTO")
public record DestinationRequestDto(
        @Schema(description = "여행지 설정 값(ex. 서울 전체 -> 서울 / 종로구 -> 서울 종로구)", example = "서울 종로구")
        String destination
) {
}
