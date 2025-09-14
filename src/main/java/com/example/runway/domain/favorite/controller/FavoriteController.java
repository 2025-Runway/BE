package com.example.runway.domain.favorite.controller;


import com.example.runway.domain.favorite.dto.FavoriteList;
import com.example.runway.domain.favorite.service.FavoriteService;
import com.example.runway.global.jwt.annotation.LoginUserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/{crsIdx}/favorite")
    @Operation(
            summary = "코스 추천 API",
            description = "코스 추천해주는 API"
    )
    public void favorite(@LoginUserId Long userId,
                         @PathVariable("crsIdx") String crsIdx) {

        favoriteService.addFavorite(userId, crsIdx);
    }

    @GetMapping("/favorite")
    @Operation(
            summary = "찜 목록 조회 API",
            description = "찜 목록을 조회"
    )
    public List<FavoriteList> getFavoriteCourse(
            @Parameter(description = "유저 아이디", example = "@")
            @LoginUserId Long userId
    ) {
        return favoriteService.getFavoriteCourseList(userId);
    }

}