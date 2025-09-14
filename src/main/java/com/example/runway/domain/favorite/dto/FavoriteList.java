package com.example.runway.domain.favorite.dto;

import com.example.runway.domain.search.dto.SearchCoursesDto;

import java.util.List;

public record FavoriteList(
        String region,
        List<SearchCoursesDto> courses
) {
}
