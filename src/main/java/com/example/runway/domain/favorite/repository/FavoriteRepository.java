package com.example.runway.domain.favorite.repository;

import com.example.runway.domain.favorite.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    boolean existsByUser_IdAndCourse_CrsIdx(Long userId, String crsIdx);
}