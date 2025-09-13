package com.example.runway.domain.user.repository;

import com.example.runway.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByKakaoId(String kakaoId);

    Optional<User> findById(Long userId);

    @Modifying
    @Query("UPDATE User u SET u.destination = :destination WHERE u.id = :userId")
    void updateDestination(Long userId, String destination);
  
    Optional<User> findByEmail(String email);

    @Query("SELECT u.destination FROM User u WHERE u.id = :userId")
    Optional<String> findDestinationById(Long userId);

}
