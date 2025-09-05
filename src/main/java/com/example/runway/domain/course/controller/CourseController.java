package com.example.runway.domain.course.controller;

import com.example.runway.domain.course.dto.CourseAnalysisDto;
import com.example.runway.domain.course.dto.CourseDto;
import com.example.runway.domain.course.dto.PopularCourseDto;
import com.example.runway.domain.course.dto.RecentCourseDto;
import com.example.runway.domain.course.service.CourseService;
import com.example.runway.global.jwt.annotation.LoginUserId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    // 코스 상세 조회 (로그인 필요 x)
    @GetMapping("/public/courses/{crsIdx}")
    public CourseDto getCourse(@PathVariable String crsIdx) {
        return courseService.getById(crsIdx);
    }

    // 코스 조회 기록을 남기는 API
    @PostMapping("/courses/{crsIdx}/history")
    public ResponseEntity<Void> updateLastViewedCourse(@LoginUserId Long userId, @PathVariable String crsIdx) {
        courseService.updateLastViewedCourse(userId, crsIdx);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 최근 본 코스 1개를 조회하는 API
    @GetMapping("/courses/history")
    public ResponseEntity<RecentCourseDto> getLastViewedCourse(@LoginUserId Long userId) {
        return courseService.getLastViewedCourse(userId)
                .map(ResponseEntity::ok) // 조회된 코스가 있으면 200 OK와 함께 DTO 반환
                .orElseGet(() -> ResponseEntity.noContent().build()); // 조회된 코스가 없으면 204 No Content 반환
    }

    // 인기 코스 조회(전국 단위, 로그인 안해도 됨)
    @GetMapping("/public/courses/popular")
    public ResponseEntity<List<PopularCourseDto>> getPopularCourses() {
        List<PopularCourseDto> result = courseService.getPopularCourses(10);
        return ResponseEntity.ok(result);
    }

    // 코스 개요
    @GetMapping("/public/courses/{crsIdx}/analysis")
    public ResponseEntity<CourseAnalysisDto> getCourseAnalysis(@PathVariable String crsIdx) {
        CourseAnalysisDto analysisDto = courseService.getCourseAnalysisById(crsIdx);
        return ResponseEntity.ok(analysisDto);
    }
}
