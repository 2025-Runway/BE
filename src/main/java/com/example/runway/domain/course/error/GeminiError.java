package com.example.runway.domain.course.error;

import com.example.runway.global.error.BaseErrorCode;
import com.example.runway.global.error.BaseErrorException;

public class GeminiError extends BaseErrorException {
    public static final GeminiError Exception = new GeminiError();

    public GeminiError() {
        super(CourseErrorCode.CourseNotFound);
    }
}

