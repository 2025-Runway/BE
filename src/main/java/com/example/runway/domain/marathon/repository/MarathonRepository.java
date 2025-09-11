package com.example.runway.domain.marathon.repository;

import com.example.runway.domain.marathon.entity.Marathon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarathonRepository extends JpaRepository<Marathon, Long> {
    List<Marathon> findAll();
}
