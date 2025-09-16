package com.example.runway.domain.course.repository;


import com.example.runway.domain.course.dto.UserProfile;
import com.example.runway.domain.course.entity.Course;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CourseRepositoryCustomImpl implements CourseRepositoryCustom {

    private final EntityManager em;

    @Override
    public UserProfile calculateUserProfile(Long userId) {
        return em.createQuery(
                        "SELECT new com.example.runway.domain.course.dto.UserProfile(AVG(c.crsLevel), AVG(c.crsTotlRqrmHour), AVG(c.crsDstnc)) " +
                                "FROM Favorite f JOIN f.course c " +
                                "WHERE f.user.id = :userId", UserProfile.class)
                .setParameter("userId", userId)
                .getSingleResult();
    }

    @Override
    public List<Course> findRecommendedCourses(Long userId, UserProfile profile) {
        return em.createQuery("""
        SELECT c
        FROM Course c
        WHERE c.crsIdx NOT IN (
            SELECT f.course.crsIdx FROM Favorite f WHERE f.user.id = :userId
        )
        ORDER BY
          ( (cast(c.crsLevel as double) - :avgCrsLevel)
          * (cast(c.crsLevel as double) - :avgCrsLevel) ) * 1.0
        + ( (cast(c.crsTotlRqrmHour as double) - :avgCrsTotlRqrmHour)
          * (cast(c.crsTotlRqrmHour as double) - :avgCrsTotlRqrmHour) ) * 0.001
        + ( (cast(c.crsDstnc as double) - :avgCrsDstnc)
          * (cast(c.crsDstnc as double) - :avgCrsDstnc) ) * 0.1
        ASC
        """, Course.class)
                .setParameter("avgCrsLevel", profile.getAvgCrsLevel())
                .setParameter("avgCrsTotlRqrmHour", profile.getAvgCrsTotlRqrmHour())
                .setParameter("avgCrsDstnc", profile.getAvgCrsDstnc())
                .setParameter("userId", userId)
                .setMaxResults(10)
                .getResultList();
    }

    @Override
    public List<Course> findPopularCoursesForRecommendation(int limit) {
        // Favorite 테이블에서 가장 많이 찜된 코스 순으로 조회
        return em.createQuery(
                        "SELECT f.course FROM Favorite f " +
                                "GROUP BY f.course.crsIdx " +
                                "ORDER BY COUNT(f.course.crsIdx) DESC", Course.class)
                .setMaxResults(limit)
                .getResultList();
    }

    @Override
    public UserProfile calculateUserProfileByRegion(Long userId, String region) {
        List<UserProfile> profiles = em.createQuery(
                        "SELECT new com.example.runway.domain.course.dto.UserProfile(AVG(c.crsLevel), AVG(c.crsTotlRqrmHour), AVG(c.crsDstnc)) " +
                                "FROM Favorite f JOIN f.course c " +
                                "WHERE f.user.id = :userId AND c.sigun LIKE :region " +
                                "GROUP BY f.user.id", UserProfile.class)
                .setParameter("userId", userId)
                .setParameter("region", region + "%")
                .getResultList();
        return profiles.isEmpty() ? null : profiles.get(0);
    }

    @Override
    public List<Course> findRecommendedCoursesByRegion(Long userId, UserProfile userProfile, String region) {
        return em.createQuery(
                        "SELECT c FROM Course c " +
                                "WHERE c.crsIdx NOT IN (SELECT f.course.crsIdx FROM Favorite f WHERE f.user.id = :userId) " +
                                "AND c.sigun LIKE :region " +
                                "ORDER BY (POW(c.crsLevel - :avgCrsLevel, 2) * 1.0) + " +
                                "(POW(c.crsTotlRqrmHour - :avgCrsTotlRqrmHour, 2) * 0.001) + " +
                                "(POW(c.crsDstnc - :avgCrsDstnc, 2) * 0.1) ASC", Course.class)
                .setParameter("avgCrsLevel", userProfile.getAvgCrsLevel())
                .setParameter("avgCrsTotlRqrmHour", userProfile.getAvgCrsTotlRqrmHour())
                .setParameter("avgCrsDstnc", userProfile.getAvgCrsDstnc())
                .setParameter("region", region + "%")
                .setMaxResults(10)
                .getResultList();
    }

    @Override
    public List<Course> findPopularCoursesForRecommendationByRegion(String region, int limit) {
        return em.createQuery(
                        "SELECT c FROM Course c " +
                            "LEFT JOIN Favorite f on c = f.course " +
                            "WHERE " + "c.sigun LIKE :region " +
                            "GROUP BY c.crsIdx " +
                            "ORDER BY COUNT(f.id) DESC", Course.class)
                .setParameter("region", region + "%")
                .setMaxResults(limit)
                .getResultList();
    }
}