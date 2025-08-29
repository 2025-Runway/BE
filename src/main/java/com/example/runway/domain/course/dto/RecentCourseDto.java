package com.example.runway.domain.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "최근 본 코스 정보 DTO")
public record RecentCourseDto(
        @Schema(description = "코스 ID")
        String crsIdx,

        @Schema(description = "코스 한글 이름")
        String crsKorNm,

        @Schema(description = "코스 거리")
        int crsDstnc,

        @Schema(description = "마지막으로 조회한 시간")
        String viewedAt

) {

}