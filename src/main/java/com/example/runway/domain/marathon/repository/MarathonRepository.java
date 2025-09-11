package com.example.runway.domain.marathon.repository;

import com.example.runway.domain.marathon.entity.Marathon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MarathonRepository extends JpaRepository<Marathon, Long> {
    @Query("""
    SELECT m
    FROM Marathon m
    ORDER BY m.id DESC
    """)
    Page<Marathon> findMarathonList(Pageable pageable);
}
