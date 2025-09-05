package com.example.runway.domain.search.repository;

import com.example.runway.domain.search.domain.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    @Query("SELECT k FROM Keyword k WHERE k.user.id = :userId ORDER BY k.created_at DESC LIMIT 10")
    List<Keyword> findByUserId(@Param("userId") Long userId);

    boolean existsByUserIdAndWord(Long userId, String word);
}
