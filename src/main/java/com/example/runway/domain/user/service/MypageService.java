package com.example.runway.domain.user.service;

import com.example.runway.domain.course.repository.CourseRepository;
import com.example.runway.domain.course.service.CourseService;
import com.example.runway.domain.user.dto.DestinationRequestDto;
import com.example.runway.domain.user.dto.DestinationResponseDto;
import com.example.runway.domain.user.error.NotFoundDestinationImage;
import com.example.runway.domain.user.error.NotFoundUser;
import com.example.runway.domain.user.repository.RegionImageRepository;
import com.example.runway.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MypageService {
    private final UserRepository userRepository;
    private final CourseService courseService;
    private final CourseRepository courseRepository;
    private final RegionImageRepository regionImageRepository;

    @Transactional
    public void destinationUpdate(Long userId, DestinationRequestDto destination) {
        userRepository.updateDestination(userId, destination.destination());
    }

    public DestinationResponseDto getDestination(Long userId) {
        String destination = userRepository.findDestinationById(userId).orElseThrow(
                () -> NotFoundUser.EXCEPTION
        );

        PageRequest pageRequest = PageRequest.of(0, 1);

        // 코스 중 있는지 조회
        List<String> url = courseRepository.findCrsImgUrlBySigun(destination, pageRequest);
        if (url.isEmpty()) {
            url = regionImageRepository.findRegionImageByName(destination, pageRequest);

            if(url.isEmpty()) throw NotFoundDestinationImage.EXCEPTION;
        };


        return new DestinationResponseDto(destination, url.get(0));
    }

    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }


}
