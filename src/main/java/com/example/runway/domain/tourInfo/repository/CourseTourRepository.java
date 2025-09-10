package com.example.runway.domain.tourInfo.repository;

import com.example.runway.domain.tourInfo.entity.CourseTour;
import com.example.runway.domain.tourInfo.entity.TourInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseTourRepository extends JpaRepository<CourseTour, Long> {
    @Query("""
        SELECT ct.tourInfo
        FROM CourseTour ct
        WHERE ct.course.crsIdx = :crsIdx
          AND (:contentTypeId IS NULL OR ct.tourInfo.contentTypeId = :contentTypeId)
    """)
    Page<TourInfo> findTourInfosByCrsIdxAndOptionalContentTypeId(
            @Param("crsIdx") String crsIdx,
            @Param("contentTypeId") String contentTypeId,
            Pageable pageable);
}
