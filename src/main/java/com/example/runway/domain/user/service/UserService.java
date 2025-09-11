package com.example.runway.domain.user.service;

import com.example.runway.domain.user.entity.User;
import com.example.runway.domain.user.error.NotFoundUser;
import com.example.runway.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> NotFoundUser.EXCEPTION);
    }
}
