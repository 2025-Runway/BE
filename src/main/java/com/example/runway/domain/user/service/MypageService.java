package com.example.runway.domain.user.service;

import com.example.runway.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MypageService {
    private final UserRepository userRepository;

    public void destinationUpdate(Long userId, String destination) {
        userRepository.updateDestination(userId, destination);
    }
}
