package com.example.runway.domain.user.repository;

import com.example.runway.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByKakaoId(String kakaoId);

    @Modifying
    @Query("UPDATE User u SET u.destination = :destination WHERE u.id = :userId")
    void updateDestination(Long userId, String destination);
  
    Optional<User> findByEmail(String email);


}
