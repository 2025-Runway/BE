package com.example.runway.domain.course.controller;

import com.example.runway.domain.course.dto.*;
import com.example.runway.domain.course.entity.Course;
import com.example.runway.domain.course.service.CourseService;
import com.example.runway.global.jwt.annotation.LoginUserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    // 코스 상세 조회 (로그인 필요 x)
    @GetMapping("/public/courses/{crsIdx}")
    @Operation(
            summary = "코스 상세 조회 API",
            description = "코스 상세 정보를 조회"
    )
    public CourseDto getCourse(@PathVariable String crsIdx) {
        return courseService.getById(crsIdx);
    }

    // 코스 상세 조회 (로그인 사용자 찜 여부 확인)
    @GetMapping("/courses/{crsIdx}")
    @Operation(
            summary = "코스 상세 조회 API",
            description = "코스 상세 정보를 조회. 로그인 시 찜 여부 확인"
    )
    public CourseDetailDto getCourse(
            @Parameter(description = "유저 아이디", example = "2")
            @LoginUserId(required = false) Long userId,
            @PathVariable String crsIdx
    ) {
        return courseService.getCourseDetail(userId, crsIdx);
    }

    // 코스 조회 기록을 남기는 API
    @PostMapping("/courses/{crsIdx}/history")
    @Operation(
            summary = "코스 조회 기록 API",
            description = "특정 유저가 조회한 코스를 기록"
    )
    public ResponseEntity<Void> updateLastViewedCourse(
            @Parameter(description = "유저 아이디", example = "2")
            @LoginUserId Long userId,
            @Parameter(description = "코스 인덱스", example = "T_CRS_MNG0000005196")
            @PathVariable String crsIdx) {
        courseService.updateLastViewedCourse(userId, crsIdx);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 최근 본 코스 1개를 조회하는 API
    @GetMapping("/courses/history")
    @Operation(
            summary = "최근 조회한 코스 1개 조회 API",
            description = "유저가 최근 조회한 1개 코스 조회"
    )
    public ResponseEntity<RecentCourseDto> getLastViewedCourse(
            @Parameter(description = "유저 아이디", example = "2")
            @LoginUserId Long userId
    ) {
        return courseService.getLastViewedCourse(userId)
                .map(ResponseEntity::ok) // 조회된 코스가 있으면 200 OK와 함께 DTO 반환
                .orElseGet(() -> ResponseEntity.noContent().build()); // 조회된 코스가 없으면 204 No Content 반환
    }

    // 인기 코스 조회(전국 단위, 로그인 안해도 됨)
    @GetMapping("/public/courses/popular")
    @Operation(
            summary = "전국 인기 코스 조회 API",
            description = "전국 인기 코스 조회 (로그인 필요 X)"
    )
    public ResponseEntity<List<CoursePreviewDto>> getPopularCourses() {
        List<CoursePreviewDto> result = courseService.getPopularCourses(10);
        return ResponseEntity.ok(result);
    }

    // 코스 개요
    @GetMapping("/public/courses/{crsIdx}/analysis")
    @Operation(
            summary = "코스 AI 분석 API",
            description = "Runway Point, 코스 데이터를 바탕으로 AI의 달리기 팁 요약"
    )
    public ResponseEntity<CourseAnalysisDto> getCourseAnalysis(
            @Parameter(description = "코스 인덱스", example = "T_CRS_MNG0000005196")
            @PathVariable String crsIdx
    ) {
        CourseAnalysisDto analysisDto = courseService.getCourseAnalysisById(crsIdx);
        return ResponseEntity.ok(analysisDto);
    }

    //추천 코스 목록
    @GetMapping("/courses/recommendations")
    @Operation(
            summary = "코스 추천 API",
            description = "코스 추천해주는 API"
    )
    public ResponseEntity<List<CoursePreviewDto>> getRecommendedCoursesForUser(
            @Parameter(description = "유저 아이디", example = "2")
            @LoginUserId Long userId
    ) {
        List<Course> recommendedCourses = courseService.getRecommendedCourses(userId);

        List<CoursePreviewDto> response = recommendedCourses.stream()
                .map(CoursePreviewDto::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // 지역 기반 추천 코스 목록
    @GetMapping("/courses/recommendations/region")
    @Operation(
            summary = "지역 기반 코스 추천 API",
            description = "사용자가 설정한 여행지 지역 기반 코스 추천"
    )
    public ResponseEntity<List<CoursePreviewDto>> getRecommendedCoursesForUserByRegion(
            @Parameter(description = "유저 아이디", example = "2")
            @LoginUserId Long userId
    ) {
        List<Course> recommendedCourses = courseService.getRecommendedCoursesByRegion(userId);
        List<CoursePreviewDto> response = recommendedCourses.stream()
                .map(CoursePreviewDto::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // 지역 기반 인기 코스 조회
    @GetMapping("/courses/popular/region")
    @Operation(
            summary = "지역 기반 인기 코스 조회 API",
            description = "지역 기반 인기 코스 조회"
    )
    public ResponseEntity<List<CoursePreviewDto>> getPopularCoursesForUserByRegion(
            @Parameter(description = "유저 아이디", example = "2")
            @LoginUserId Long userId
    ) {
        List<Course> courses = courseService.getPopularCoursesByRegion(userId, 10);
        List<CoursePreviewDto> response = courses.stream()
                .map(CoursePreviewDto::from)
                .toList();

        return ResponseEntity.ok(response);
    }

}
