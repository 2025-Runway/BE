package com.example.runway.domain.course.repository;

import com.example.runway.domain.course.entity.Course;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, String>, JpaSpecificationExecutor<Course>, CourseRepositoryCustom {
    @Query("SELECT f.course FROM Favorite f GROUP BY f.course ORDER BY COUNT(f.course) DESC")
    List<Course> findPopularCourses(Pageable pageable);

    Optional<Course> findByCrsIdx(String crsIdx);

    @Query("SELECT f.course FROM Favorite f GROUP BY f.course HAVING f.course.sigun = :sigun ORDER BY COUNT(f.course) DESC")
    List<Course> findPopularCoursesBySigun(String sigun, Pageable pageable);

    @Query("""
    SELECT c.crsImgUrl
    FROM Course c
    WHERE c.sigun LIKE concat('%', :sigun, '%')
    AND c.crsImgUrl IS NOT NULL
    """)
    List<String> findCrsImgUrlBySigun(String sigun, Pageable pageable);

}
