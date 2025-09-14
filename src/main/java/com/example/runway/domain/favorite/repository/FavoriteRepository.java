package com.example.runway.domain.favorite.repository;

import com.example.runway.domain.course.entity.Course;
import com.example.runway.domain.favorite.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    boolean existsByUser_IdAndCourse_CrsIdx(Long userId, String crsIdx);
    long countByUserId(Long userId);
    @Query("SELECT f.course " +
            "FROM Favorite f WHERE f.user.id = :userId")
    List<Course> findCourseByUserId(Long userId);

}