package com.example.runway.domain.search.repository;

import com.example.runway.domain.course.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface SearchRepository extends JpaRepository<Course, Long> {
    @Query("SELECT c FROM Course c WHERE c.crsKorNm LIKE %:q% OR c.sigun LIKE %:q%")
    Page<Course> findByCrsKorNmOrSigun(@Param("q")String q, Pageable pageable);

    Optional<Course> findBycrsIdx(String crsIdx);
}
