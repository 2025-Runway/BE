package com.example.runway.domain.search.controller;

import com.example.runway.domain.search.dto.SearchCourseDto;
import com.example.runway.domain.search.dto.SearchCoursesDto;
import com.example.runway.domain.search.service.KeywordService;
import com.example.runway.domain.search.service.SearchService;
import com.example.runway.global.jwt.annotation.LoginUserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("public/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;
    private final KeywordService keywordService;

    @GetMapping("")
    @Operation(
            summary = "검색 코스 목록 조회 API",
            description = "검색 코스 목록을 조회"
    )
    public Page<SearchCoursesDto> searchCourses(
            @Parameter(description = "유저 아이디(없어도 됨)", example = "2")
            @LoginUserId(required = false) Long userId,
            @Parameter(description = "검색어", example = "파랑")
            @RequestParam String q,
            @Parameter(description = "페이지 번호", example = "1")
            @RequestParam int page
    ) {
        keywordService.addKeyword(userId, q.trim());
        return searchService.coursesSearch(q.trim(), page-1);
    }

    @GetMapping("/{crsIdx}")
    @Operation(
            summary = "검색 코스 상세 조회 API",
            description = "검색 코스 상세를 조회"
    )
    public SearchCourseDto searchCourse(
            @Parameter(description = "코스 인덱스", example = "T_CRS_MNG0000005196")
            @PathVariable String crsIdx
    ) {
        return searchService.courseSearch(crsIdx);
    }


}
