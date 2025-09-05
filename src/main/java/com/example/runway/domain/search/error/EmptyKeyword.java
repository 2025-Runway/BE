package com.example.runway.domain.search.error;

import com.example.runway.global.error.BaseErrorException;

public class EmptyKeyword extends BaseErrorException {
    public static final EmptyKeyword EXCEPTION = new EmptyKeyword();
    public EmptyKeyword() {
        super(SearchErrorCode.EMPTY_KEYWORD);
    }
}
