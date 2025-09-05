package com.example.runway.domain.search.error;

import com.example.runway.global.error.BaseErrorException;

public class NoSearchResult extends BaseErrorException {
    public static final NoSearchResult EXCEPTION = new NoSearchResult();
    public NoSearchResult() {
        super(SearchErrorCode.NO_SEARCH_RESULT);
    }
}
