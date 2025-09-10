package com.example.runway.domain.tourInfo.controller;

import com.example.runway.domain.tourInfo.dto.TourInfoDto;
import com.example.runway.domain.tourInfo.service.TourInfoService;
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
    public Page<TourInfoDto> getTourInfo(
            @PathVariable String crsIdx,
            @RequestParam(required = false) String contentType,
            @RequestParam(required = true) int page) {
        return tourInfoService.getByCrsIdx(crsIdx, contentType, page);
    }
}
