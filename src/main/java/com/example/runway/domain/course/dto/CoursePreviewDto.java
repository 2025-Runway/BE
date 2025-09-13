package com.example.runway.domain.course.dto;

import com.example.runway.domain.course.entity.Course;

/**
 * 인기 코스, 추천 코스 등 목록성 API 응답을 위한 공통 DTO
 */
public record CoursePreviewDto(
        String sigun,
        String crsKorNm,
        String crsIdx,
        String crsImgUrl
) {
        public static CoursePreviewDto from(Course course) {
                return new CoursePreviewDto(
                        course.getSigun(),
                        course.getCrsKorNm(),
                        course.getCrsIdx(),
                        course.getCrsImgUrl()
                );
        }
}