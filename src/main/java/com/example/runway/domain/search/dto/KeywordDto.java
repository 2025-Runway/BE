package com.example.runway.domain.search.dto;

import com.example.runway.domain.search.domain.Keyword;

public record KeywordDto(
        Long KeywordId,
        String keyword
) {
    public static KeywordDto from(Keyword keyword) {
        return new KeywordDto(
                keyword.getKeywordId(),
                keyword.getWord()
        );
    }
}
