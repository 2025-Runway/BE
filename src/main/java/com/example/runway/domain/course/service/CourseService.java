package com.example.runway.domain.course.service;

import com.example.runway.domain.course.dto.*;
import com.example.runway.domain.course.entity.Course;
import com.example.runway.domain.course.error.CourseFailed;
import com.example.runway.domain.course.error.PopularCourseNotFound;
import com.example.runway.domain.course.repository.CourseRepository;
import com.example.runway.domain.favorite.repository.FavoriteRepository;
import com.example.runway.domain.user.entity.User;
import com.example.runway.domain.user.exception.UserFailed;
import com.example.runway.domain.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final GeminiService geminiService;
    private final ObjectMapper objectMapper;
    private final FavoriteRepository favoriteRepository;

    private static final Map<String, String> REGION_MAP = Map.ofEntries(
            Map.entry("서울특별시", "서울"),
            Map.entry("부산광역시", "부산"),
            Map.entry("대구광역시", "대구"),
            Map.entry("인천광역시", "인천"),
            Map.entry("광주광역시", "광주"),
            Map.entry("대전광역시", "대전"),
            Map.entry("울산광역시", "울산"),
            Map.entry("세종특별자치시", "세종"),
            Map.entry("경기도", "경기"),
            Map.entry("강원도", "강원"),
            Map.entry("충청북도", "충북"),
            Map.entry("충청남도", "충남"),
            Map.entry("전라북도", "전북"),
            Map.entry("전라남도", "전남"),
            Map.entry("경상북도", "경북"),
            Map.entry("경상남도", "경남"),
            Map.entry("제주특별자치도", "제주")
    );


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
    public List<CoursePreviewDto> getPopularCourses(int size) {
        PageRequest pageRequest = PageRequest.of(0, size);
        List<Course> popularCourses = courseRepository.findPopularCourses(pageRequest);

        if (popularCourses.isEmpty()) {
            throw PopularCourseNotFound.Exception;
        }

        // Course 엔티티를 PopularCourseDto 레코드로 변환합니다.
        return popularCourses.stream()
                .map(course -> new CoursePreviewDto(
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

    /**
     * 사용자 맞춤 코스를 추천하는 메소드
     * @param userId 사용자 ID
     * @return 추천하는 Course 엔티티 리스트
     */
    @Transactional(readOnly = true)
    public List<Course> getRecommendedCourses(Long userId) {
        // 1. 사용자의 찜 개수 확인
        long favoriteCount = favoriteRepository.countByUserId(userId);

        // 2. 찜 개수에 따라 로직 분기
        if (favoriteCount > 0) {
            // 찜한 코스가 있는 경우: 유사도 기반 추천
            UserProfile userProfile = courseRepository.calculateUserProfile(userId);
            return courseRepository.findRecommendedCourses(userId, userProfile);
        } else {
            // 찜한 코스가 없는 경우: 콜드 스타트 (인기 코스 추천)
            // 참고: 기존 getPopularCourses는 DTO를 반환하므로, 추천 로직에서는 엔티티를 직접 다루기 위해
            // Repository의 메서드를 직접 호출합니다.
            return courseRepository.findPopularCoursesForRecommendation(10);
        }
    }

    private String getRegionAbbreviation(String destination) {
        if (destination == null || destination.isBlank()) {
            return null;
        }
        String mainRegion = destination.split(" ")[0]; // "부산광역시 남구" -> "부산광역시"
        return REGION_MAP.get(mainRegion); // "부산광역시" -> "부산"
    }

    /**
     * 사용자 지역 기반 맞춤 코스를 추천하는 메소드
     * @param userId 사용자 ID
     * @return 지역 기반으로 추천하는 Course 엔티티 리스트
     */
    @Transactional(readOnly = true)
    public List<Course> getRecommendedCoursesByRegion(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> UserFailed.Exception);

        // 헬퍼 메서드를 사용하여 약칭을 가져옴
        String regionAbbreviation = getRegionAbbreviation(user.getDestination());

        // 지역 설정이 없거나, 맵에 없는 지역명일 경우 전국 단위 추천 실행
        if (regionAbbreviation == null) {
            log.info("사용자 ID {}: 유효한 지역 정보 없음. 전국 단위 추천 실행.", userId);
            return getRecommendedCourses(userId);
        }

        log.info("사용자 ID {}: 지역 '{}' 기반 추천 실행.", userId, regionAbbreviation);

        long favoriteCount = favoriteRepository.countByUserId(userId);

        if (favoriteCount > 0) {
            UserProfile userProfile = courseRepository.calculateUserProfileByRegion(userId, regionAbbreviation);
            if (userProfile != null) {
                log.info("사용자 ID {}: 지역 기반 프로필 생성 완료. 유사도 추천 실행.", userId);
                return courseRepository.findRecommendedCoursesByRegion(userId, userProfile, regionAbbreviation);
            }
            log.info("사용자 ID {}: 지역 내 찜 기록 없음. 지역 기반 콜드 스타트 실행.", userId);
        }

        List<Course> regionalPopularCourses = courseRepository.findPopularCoursesForRecommendationByRegion(regionAbbreviation, 10);

        if (regionalPopularCourses.isEmpty()) {
            log.warn("사용자 ID {}: 지역 '{}' 내 인기 코스 없음. 전국 단위 인기 코스로 대체.", userId, regionAbbreviation);
            return courseRepository.findPopularCoursesForRecommendation(10);
        }

        return regionalPopularCourses;
    }

}
