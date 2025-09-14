package com.example.runway.domain.tourInfo.controller;

import com.example.runway.domain.tourInfo.dto.TourInfoDto;
import com.example.runway.domain.tourInfo.service.TourInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/public/tourinfo")
@RequiredArgsConstructor
public class TourInfoController {
    private final TourInfoService tourInfoService;

    @GetMapping("/{crsIdx}")
    @Operation(
            summary = "주변 정보 목록 조회 API",
            description = "주변 정보 목록을 조회"
    )
    public Page<TourInfoDto> getTourInfo(
            @Parameter(description = "코스 인덱스", example = "T_CRS_MNG0000005196")
            @PathVariable String crsIdx,
            @Parameter(description = "카테고리를 한글로 입력해주세요", example = "관광지")
            @RequestParam(required = false) String contentType,
            @Parameter(description = "페이지 번호", example = "1")
            @RequestParam(required = true) int page) {
        return tourInfoService.getByCrsIdx(crsIdx, contentType, page);
    }
}
