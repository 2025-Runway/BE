package com.example.runway.domain.search.dto;

import com.example.runway.domain.course.entity.Course;

public record SearchCourseDto(
        String crsIdx,
        String crsName,
        String gpxPath,
        String content
) {
    public static SearchCourseDto from(Course course) {
        return new SearchCourseDto(
                course.getCrsIdx(),
                course.getCrsKorNm(),
                course.getGpxPath(),
                course.getRoute().getLineMsg()
        );
    }
}
