package com.example.runway.domain.search.controller;

import com.example.runway.domain.search.dto.SearchCourseDto;
import com.example.runway.domain.search.dto.SearchCoursesDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("public/search")
@RequiredArgsConstructor
public class SearchController {

    @GetMapping("")
    public List<SearchCoursesDto> searchCourses(@RequestParam String q) {
        return null;
    }

    @GetMapping("/{crsIdx}")
    public SearchCourseDto searchCourse(@PathVariable String crsIdx) {
        return null;
    }


}
