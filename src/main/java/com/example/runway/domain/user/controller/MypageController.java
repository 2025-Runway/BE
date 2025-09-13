package com.example.runway.domain.user.controller;

import com.example.runway.domain.course.dto.CourseDto;
import com.example.runway.domain.user.dto.DestinationRequestDto;
import com.example.runway.domain.user.dto.DestinationResponseDto;
import com.example.runway.domain.user.service.MypageService;
import com.example.runway.global.jwt.annotation.LoginUserId;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mypage")
public class MypageController {
    private final MypageService mypageService;

    public MypageController(MypageService mypageService) {
        this.mypageService = mypageService;
    }

    @PatchMapping("/destnination")
    public void updateDestination(
            @LoginUserId Long userId,
            @RequestBody DestinationRequestDto destination)
    {
        mypageService.destinationUpdate(userId, destination);
    }

    @GetMapping("/destnination")
    public DestinationResponseDto getDestination(@LoginUserId Long userId) {
        return mypageService.getDestination(userId);
    }

    @DeleteMapping("")
    public void deleteUser(@LoginUserId Long userId) {
        mypageService.deleteUser(userId);
    }
}
