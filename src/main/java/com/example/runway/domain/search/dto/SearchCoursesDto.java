package com.example.runway.domain.search.dto;

import com.example.runway.domain.course.entity.Course;

public record SearchCoursesDto(
        String crsIdx,
        String crsName,
        String address
) {
    public static SearchCoursesDto from(Course course) {
        return new SearchCoursesDto(
                course.getCrsIdx(),
                course.getCrsKorNm(),
                course.getSigun()
        );
    }
}
