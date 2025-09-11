package com.example.runway.domain.weather.controller;


import com.example.runway.domain.weather.dto.WeatherDetailResponse;
import com.example.runway.domain.weather.dto.WeatherResponseDto;
import com.example.runway.domain.weather.dto.WeeklyWeatherDto;
import com.example.runway.domain.weather.service.WeatherService;
import com.example.runway.global.jwt.annotation.LoginUserId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping("/public/weather")
    public ResponseEntity<WeatherResponseDto> getWeather(
            @RequestParam("lat") double lat, // 위도
            @RequestParam("lon") double lon) { // 경도

        WeatherResponseDto weatherDto = weatherService.getWeather(lat, lon);
        return ResponseEntity.ok(weatherDto);
    }

    @GetMapping("/public/weather/details")
    public ResponseEntity<WeatherDetailResponse> getWeatherDetails(
            @RequestParam("lat") double lat,
            @RequestParam("lon") double lon) {
        WeatherDetailResponse weatherDetailResponse = weatherService.getWeatherDetails(lat, lon);
        return ResponseEntity.ok(weatherDetailResponse);
    }

    @GetMapping("/public/weather/weekly")
    public ResponseEntity<List<WeeklyWeatherDto>> getWeeklyWeather(
            @RequestParam("lat") double lat,
            @RequestParam("lon") double lon) {
        List<WeeklyWeatherDto> weeklyWeather = weatherService.getWeeklyWeather(lat, lon);
        return ResponseEntity.ok(weeklyWeather);
    }

    /**
     * 사용자가 설정한 지역의 상세 날씨 조회
     */
    @GetMapping("/weather/details")
    public ResponseEntity<WeatherDetailResponse> getWeatherDetailsByDestination(@LoginUserId Long userId) {
        WeatherDetailResponse weatherDetailResponse = weatherService.getWeatherDetailsByDestination(userId);
        return ResponseEntity.ok(weatherDetailResponse);
    }

    /**
     * 사용자가 설정한 지역의 주간 날씨 조회
     */
    @GetMapping("/weather/weekly")
    public ResponseEntity<List<WeeklyWeatherDto>> getWeeklyWeatherByDestination(@LoginUserId Long userId) {
        List<WeeklyWeatherDto> weeklyWeather = weatherService.getWeeklyWeatherByDestination(userId);
        return ResponseEntity.ok(weeklyWeather);
    }
}