package com.example.runway.domain.marathon.controller;

import com.example.runway.domain.marathon.dto.MarathonListDto;
import com.example.runway.domain.marathon.service.MarathonService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/public/marathons")
@RequiredArgsConstructor
public class MarathonController {
    private final MarathonService marathonService;

    @GetMapping("")
    public List<MarathonListDto> getMarathons() {
        return marathonService.marathonList();
    }
}
