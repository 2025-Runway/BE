package com.example.runway.domain.search.error;

import com.example.runway.global.error.BaseErrorException;

public class NotFoundCourse extends BaseErrorException {
    public static final NotFoundCourse EXCEPTION = new NotFoundCourse();
    public NotFoundCourse() {
        super(SearchErrorCode.NOT_FOUND_COURSE);
    }
}
