package com.example.runway.domain.user.service;

import com.example.runway.domain.user.dto.DestinationDto;
import com.example.runway.domain.user.dto.DestninationDto;
import com.example.runway.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.print.attribute.standard.Destination;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MypageService {
    private final UserRepository userRepository;

    @Transactional
    public void destinationUpdate(Long userId, DestinationDto destination) {
        userRepository.updateDestination(userId, destination.destination());
    }
}
