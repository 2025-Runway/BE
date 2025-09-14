package com.example.runway.domain.favorite.controller;


import com.example.runway.domain.favorite.service.FavoriteService;
import com.example.runway.global.jwt.annotation.LoginUserId;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

}