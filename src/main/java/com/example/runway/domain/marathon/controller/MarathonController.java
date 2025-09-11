package com.example.runway.domain.marathon.controller;

import com.example.runway.domain.marathon.dto.MarathonDto;
import com.example.runway.domain.marathon.dto.MarathonListDto;
import com.example.runway.domain.marathon.service.MarathonService;
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
    public Page<MarathonListDto> getMarathons(@RequestParam(required = true) int page) {
        return marathonService.marathonList(page);
    }

    @GetMapping("/{marathonId}")
    public MarathonDto getMarathon(@PathVariable Long marathonId) {
         return marathonService.getMarathon(marathonId);
    }
}
