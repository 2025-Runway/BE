package com.example.runway.domain.search.controller;

import com.example.runway.domain.search.dto.KeywordDto;
import com.example.runway.domain.search.service.KeywordService;
import com.example.runway.global.jwt.annotation.LoginUserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/keywords")
@RequiredArgsConstructor
public class KeywordController {

    private final KeywordService keywordService;

    @GetMapping("")
    @Operation(
            summary = "검색어 목록 조회 API",
            description = "검색어 목록을 조회"
    )
    public List<KeywordDto> getKeywords(
            @Parameter(description = "유저 아이디", example = "2")
            @LoginUserId Long userId) {
        return keywordService.getKeywords(userId);
    }

    @DeleteMapping("/{keywordId}")
    @Operation(
            summary = "검색 내역 삭제 API",
            description = "검색 내역을 삭제"
    )
    public void deleteKeyword(
            @Parameter(description = "유저 아이디", example = "2")
            @LoginUserId Long userId,
            @Parameter(description = "검색어 아이디", example = "3")
            @PathVariable Long keywordId) {
        keywordService.deleteKeyword(userId, keywordId);
    }
}
