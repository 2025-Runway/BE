package com.example.runway.domain.marathon.repository;

import com.example.runway.domain.marathon.dto.Price;
import com.example.runway.domain.marathon.entity.Marathon;
import com.example.runway.domain.marathon.entity.MarathonType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarathonTypeRepository extends JpaRepository<MarathonType, Long> {
    @Query("""
      select mt.name
      from MarathonType mt
      where mt.marathon = :marathon
    """)
    List<String> findNamesByMarathon(@Param("marathon") Marathon marathon);

    List<MarathonType> findByMarathon(Marathon marathon);
}
