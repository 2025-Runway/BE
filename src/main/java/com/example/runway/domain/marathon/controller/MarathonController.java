package com.example.runway.domain.marathon.controller;

import com.example.runway.domain.marathon.dto.MarathonDto;
import com.example.runway.domain.marathon.dto.MarathonListDto;
import com.example.runway.domain.marathon.service.MarathonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/public/marathons")
@RequiredArgsConstructor
public class MarathonController {
    private final MarathonService marathonService;

    @GetMapping("")
    @Operation(
            summary = "마라톤 목록 조회",
            description = "마라톤 목록을 조회"
    )
    public Page<MarathonListDto> getMarathons(
            @Parameter(description = "페이지 번호", example = "1")
            @RequestParam(required = true) int page
    ) {
        return marathonService.marathonList(page);
    }

    @GetMapping("/{marathonId}")
    @Operation(
            summary = "마라톤 상세 조회 API",
            description = "마라톤 상세 조회"
    )
    public MarathonDto getMarathon(
            @Parameter(description = "마라톤 아이디", example = "193")
            @PathVariable Long marathonId) {
         return marathonService.getMarathon(marathonId);
    }
}
