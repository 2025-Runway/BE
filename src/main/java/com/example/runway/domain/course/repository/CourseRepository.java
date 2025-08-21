package com.example.runway.domain.course.repository;

import com.example.runway.domain.course.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CourseRepository extends JpaRepository<Course, String>, JpaSpecificationExecutor<Course> {
}
