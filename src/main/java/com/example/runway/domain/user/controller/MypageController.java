package com.example.runway.domain.user.controller;

import com.example.runway.domain.course.dto.CourseDto;
import com.example.runway.domain.user.dto.DestinationDto;
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
            @RequestBody DestinationDto destination)
    {
        mypageService.destinationUpdate(userId, destination);
    }
}
