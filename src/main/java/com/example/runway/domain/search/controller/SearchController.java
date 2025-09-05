package com.example.runway.domain.search.controller;

import com.example.runway.domain.search.dto.SearchCourseDto;
import com.example.runway.domain.search.dto.SearchCoursesDto;
import com.example.runway.domain.search.service.KeywordService;
import com.example.runway.domain.search.service.SearchService;
import com.example.runway.global.jwt.annotation.LoginUserId;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("public/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;
    private final KeywordService keywordService;

    @GetMapping("")
    public List<SearchCoursesDto> searchCourses(@LoginUserId(required = false) Long userId, @RequestParam String q) {
        keywordService.addKeyword(userId, q);
        return searchService.coursesSearch(q);
    }

    @GetMapping("/{crsIdx}")
    public SearchCourseDto searchCourse(@PathVariable String crsIdx) {
        return searchService.courseSearch(crsIdx);
    }


}
