package com.example.runway.domain.course.controller;

import com.example.runway.domain.course.dto.CourseDto;
import com.example.runway.domain.course.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("public/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping("/{crsIdx}")
    public CourseDto getCourse(@PathVariable String crsIdx) {
        return courseService.getById(crsIdx);
    }
}
