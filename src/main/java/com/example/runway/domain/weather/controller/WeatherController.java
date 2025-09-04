package com.example.runway.domain.weather.controller;


import com.example.runway.domain.weather.dto.WeatherResponseDto;
import com.example.runway.domain.weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequiredArgsConstructor
@RequestMapping("/public/weather")
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping
    public ResponseEntity<WeatherResponseDto> getWeather(
            @RequestParam("lat") double lat, // 위도
            @RequestParam("lon") double lon) { // 경도

        WeatherResponseDto weatherDto = weatherService.getWeather(lat, lon);
        return ResponseEntity.ok(weatherDto);
    }
}