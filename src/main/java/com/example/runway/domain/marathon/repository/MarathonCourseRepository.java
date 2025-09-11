package com.example.runway.domain.marathon.repository;

import com.example.runway.domain.course.entity.Course;
import com.example.runway.domain.marathon.entity.MarathonCourse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarathonCourseRepository extends JpaRepository<MarathonCourse, Long> {
    @Query(
        """
        SELECT mc.course
        FROM MarathonCourse mc
        WHERE mc.marathon.id = :marathonId
        """
    )
    List<Course> findCourseByMarathonId(@Param("marathonId") Long marathonId, Pageable pageable);
}
