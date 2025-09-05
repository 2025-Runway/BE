package com.example.runway.domain.course.error;

import com.example.runway.global.error.BaseErrorException;

public class PopularCourseNotFound extends BaseErrorException {
    public static final PopularCourseNotFound Exception = new PopularCourseNotFound();

    public PopularCourseNotFound() {
        super(CourseErrorCode.PopularCourseNotFound);
    }
}
