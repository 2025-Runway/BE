package com.example.runway.domain.course.repository;

import com.example.runway.domain.course.dto.UserProfile;
import com.example.runway.domain.course.entity.Course;

import java.util.List;

public interface CourseRepositoryCustom {
    UserProfile calculateUserProfile(Long userId);
    List<Course> findRecommendedCourses(Long userId, UserProfile userProfile);
    List<Course> findPopularCoursesForRecommendation(int limit);


    //사용자 위치 기반
    UserProfile calculateUserProfileByRegion(Long userId, String region);
    List<Course> findRecommendedCoursesByRegion(Long userId, UserProfile userProfile, String region);
    List<Course> findPopularCoursesForRecommendationByRegion(String region, int limit);
}