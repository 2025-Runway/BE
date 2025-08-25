package com.example.runway.domain.course.error;

import com.example.runway.global.error.BaseErrorException;

public class CourseFailed extends BaseErrorException {

    public static final CourseFailed Exception = new CourseFailed();


    public CourseFailed() {
        super(CourseErrorCode.CourseNotFound);
    }
}
