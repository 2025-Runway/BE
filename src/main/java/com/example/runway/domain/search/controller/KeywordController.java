package com.example.runway.domain.search.controller;

import com.example.runway.domain.search.dto.KeywordDto;
import com.example.runway.domain.search.service.KeywordService;
import com.example.runway.global.jwt.annotation.LoginUserId;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/keywords")
@RequiredArgsConstructor
public class KeywordController {

    private final KeywordService keywordService;

    @GetMapping("")
    public List<KeywordDto> getKeywords(@LoginUserId Long userId) {
        return keywordService.getKeywords(userId);
    }
}
