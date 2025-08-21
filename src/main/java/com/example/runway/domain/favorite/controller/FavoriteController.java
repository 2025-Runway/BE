package com.example.runway.domain.favorite.controller;


import com.example.runway.domain.favorite.service.FavoriteService;
import com.example.runway.global.jwt.annotation.LoginUserId;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/{crsIdx}/favorite")
    public void favorite(@LoginUserId Long userId,
                         @PathVariable("crsIdx") String crsIdx) {

        favoriteService.addFavorite(userId, crsIdx);
    }

}