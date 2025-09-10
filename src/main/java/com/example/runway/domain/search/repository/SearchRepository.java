package com.example.runway.domain.search.repository;

import com.example.runway.domain.course.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SearchRepository extends JpaRepository<Course, Long> {
    @Query("SELECT c FROM Course c WHERE c.crsKorNm LIKE %:q% OR c.sigun LIKE %:q%")
    List<Course> findByCrsKorNmOrSigun(@Param("q")String q);

    Optional<Course> findBycrsIdx(String crsIdx);
}
