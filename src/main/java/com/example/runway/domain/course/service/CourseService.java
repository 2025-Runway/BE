package com.example.runway.domain.course.service;

import com.example.runway.domain.course.dto.CourseAnalysisDto;
import com.example.runway.domain.course.dto.CourseDto;
import com.example.runway.domain.course.dto.PopularCourseDto;
import com.example.runway.domain.course.dto.RecentCourseDto;
import com.example.runway.domain.course.entity.Course;
import com.example.runway.domain.course.error.CourseFailed;
import com.example.runway.domain.course.error.PopularCourseNotFound;
import com.example.runway.domain.course.repository.CourseRepository;
import com.example.runway.domain.user.entity.User;
import com.example.runway.domain.user.exception.UserFailed;
import com.example.runway.domain.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final GeminiService geminiService;
    private final ObjectMapper objectMapper;



    public CourseDto getById(String crsIdx) {
        Course c = courseRepository.findById(crsIdx)
                .orElseThrow(() -> CourseFailed.Exception);
        return CourseDto.from(c);
    }

    /**
     * 사용자의 마지막 코스 조회 기록을 업데이트하는 메소드
     */
    @Transactional
    public void updateLastViewedCourse(Long userId, String crsIdx) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserFailed.Exception);
        Course course = courseRepository.findById(crsIdx)
                .orElseThrow(() -> CourseFailed.Exception);

        // User 엔티티의 편의 메소드를 사용하여 마지막으로 본 코스를 업데이트
        user.updateLastViewedCourse(course);
    }


    /**
     * 사용자가 마지막으로 본 코스 정보를 조회하는 메소드
     */
    @Transactional(readOnly = true)
    public Optional<RecentCourseDto> getLastViewedCourse(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserFailed.Exception);

        // User 객체에서 마지막으로 본 코스(Course)와 시간(LocalDateTime)을 가져옵니다.
        Course lastViewedCourse = user.getLastViewedCourse();
        LocalDateTime lastViewedAt = user.getLastViewedAt();

        // 조회 기록이 없으면 (코스가 null이면) 빈 Optional을 반환합니다.
        if (lastViewedCourse == null) {
            return Optional.empty();
        }
        // 날짜 형식을 정의
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

        // LocalDateTime 객체를 정의된 형식의 문자열로 변환
        String formattedDate = lastViewedAt.format(formatter);

        // 코스 정보와 조회 시간으로 RecentCourseDto를 생성하여 반환합니다.
        return Optional.of(new RecentCourseDto(
                lastViewedCourse.getCrsIdx(),
                lastViewedCourse.getCrsKorNm(),
                lastViewedCourse.getCrsDstnc(),
                formattedDate
        ));
    }

    /**
     * 인기 코스 목록을 조회하는 메소드 (찜 많은 순)
     */
    @Transactional(readOnly = true)
    public List<PopularCourseDto> getPopularCourses(int size) {
        PageRequest pageRequest = PageRequest.of(0, size);
        List<Course> popularCourses = courseRepository.findPopularCourses(pageRequest);

        if (popularCourses.isEmpty()) {
            throw PopularCourseNotFound.Exception;
        }

        // Course 엔티티를 PopularCourseDto 레코드로 변환합니다.
        return popularCourses.stream()
                .map(course -> new PopularCourseDto(
                        course.getSigun(),
                        course.getCrsKorNm(),
                        course.getCrsIdx(),
                        course.getCrsImgUrl()
                ))
                .collect(Collectors.toList());
    }

    /**
     * crsIdx로 코스를 찾아 AI 분석을 요청하고, 최종 DTO를 반환합니다.
     * @param crsIdx 코스 ID
     * @return CourseAnalysisDto
     */
    public CourseAnalysisDto getCourseAnalysisById(String crsIdx) {
        Course course = courseRepository.findByCrsIdx(crsIdx)
                .orElseThrow(() -> new IllegalArgumentException("해당 코스를 찾을 수 없습니다. id=" + crsIdx));

        try {
            String jsonResponse = geminiService.generateCourseAnalysis(course);

            return objectMapper.readValue(jsonResponse, CourseAnalysisDto.class);

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("AI 코스 분석 중 오류가 발생했습니다.");
        }
    }
}
