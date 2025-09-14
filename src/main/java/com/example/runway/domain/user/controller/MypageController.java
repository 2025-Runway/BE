package com.example.runway.domain.user.controller;

import com.example.runway.domain.user.dto.DestinationRequestDto;
import com.example.runway.domain.user.dto.MypageResponseDto;
import com.example.runway.domain.user.service.MypageService;
import com.example.runway.global.jwt.annotation.LoginUserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mypage")
public class MypageController {
    private final MypageService mypageService;

    public MypageController(MypageService mypageService) {
        this.mypageService = mypageService;
    }

    @PatchMapping("/destnination")
    @Operation(
            summary = "여행지 설정 API",
            description = "여행지 설정"
    )
    public void updateDestination(
            @Parameter(description = "유저 아이디", example = "2")
            @LoginUserId Long userId,
            @RequestBody DestinationRequestDto destination)
    {
        mypageService.destinationUpdate(userId, destination);
    }

    @GetMapping("")
    @Operation(
            summary = "마이페이지 조회 API",
            description = "마이페이지를 조회"
    )
    public MypageResponseDto getDestination(
            @Parameter(description = "유저 아이디", example = "2")
            @LoginUserId Long userId) {
        return mypageService.getDestination(userId);
    }

    @DeleteMapping("")
    @Operation(
            summary = "회원 탈퇴 API",
            description = "회원 탈퇴"
    )
    public void deleteUser(
            @Parameter(description = "유저 아이디(테스트 하지마세요...)", example = "2")
            @LoginUserId Long userId) {
        mypageService.deleteUser(userId);
    }
}
