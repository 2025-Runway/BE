package com.example.runway.domain.user.repository;

import com.example.runway.domain.user.entity.RegionImage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegionImageRepository extends JpaRepository<RegionImage, Long> {

    @Query("SELECT r.url FROM RegionImage r WHERE r.name LIKE concat('%', :name, '%')")
    List<String> findRegionImageByName(String name, Pageable pageable);
}
