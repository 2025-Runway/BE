package com.example.runway.domain.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "인기 코스 정보 응답 DTO")
public record PopularCourseDto(
        @Schema(description = "시/군 이름")
        String sigun,

        @Schema(description = "코스 한글 이름")
        String crsKorNm,

        @Schema(description = "코스 idx")
        String crsIdx,

        @Schema(description = "코스 썸네일 이미지 URL")
        String crsImgUrl
) {
}