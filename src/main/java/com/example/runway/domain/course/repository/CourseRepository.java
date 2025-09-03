package com.example.runway.domain.course.repository;

import com.example.runway.domain.course.entity.Course;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, String>, JpaSpecificationExecutor<Course> {
    @Query("SELECT f.course FROM Favorite f GROUP BY f.course ORDER BY COUNT(f.course) DESC")
    List<Course> findPopularCourses(Pageable pageable);
}
