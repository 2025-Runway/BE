package com.example.runway.domain.weather.controller;


import com.example.runway.domain.weather.dto.WeatherDetailResponse;
import com.example.runway.domain.weather.dto.WeatherResponseDto;
import com.example.runway.domain.weather.dto.WeeklyWeatherDto;
import com.example.runway.domain.weather.service.WeatherService;
import com.example.runway.global.jwt.annotation.LoginUserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
    @Operation(
            summary = "첫 화면 날씨 API",
            description = "첫화면 날씨 API. 온도, 대기질, 하늘상태 반환"
    )
    public ResponseEntity<WeatherResponseDto> getWeather(
            @Parameter(description = "위도", example = "37.12312")
            @RequestParam("lat") double lat, // 위도
            @Parameter(description = "경도", example = "127.079997")
            @RequestParam("lon") double lon) { // 경도

        WeatherResponseDto weatherDto = weatherService.getWeather(lat, lon);
        return ResponseEntity.ok(weatherDto);
    }


    @GetMapping("/public/weather/details")
    @Operation(
            summary = "날씨탭 : 오늘 날씨 상세 조회 API",
            description = "특정 좌표의 현재 및 오늘의 상세 날씨 정보를 조회합니다."
    )
    public ResponseEntity<WeatherDetailResponse> getWeatherDetails(
            @Parameter(description = "위도", example = "37.12312")
            @RequestParam("lat") double lat,
            @Parameter(description = "경도", example = "127.079997")
            @RequestParam("lon") double lon) {
        WeatherDetailResponse weatherDetailResponse = weatherService.getWeatherDetails(lat, lon);
        return ResponseEntity.ok(weatherDetailResponse);
    }

    @GetMapping("/public/weather/weekly")
    @Operation(
            summary = "날씨탭 : 주간 날씨 API",
            description = "특정 좌표의 오늘부터 6일 후까지, 총 7일간의 주간 예보를 조회합니다."
    )
    public ResponseEntity<List<WeeklyWeatherDto>> getWeeklyWeather(
            @Parameter(description = "위도", example = "37.12312")
            @RequestParam("lat") double lat,
            @Parameter(description = "경도", example = "127.079997")
            @RequestParam("lon") double lon) {
        List<WeeklyWeatherDto> weeklyWeather = weatherService.getWeeklyWeather(lat, lon);
        return ResponseEntity.ok(weeklyWeather);
    }

    /**
     * 사용자가 설정한 지역의 상세 날씨 조회
     */
    @GetMapping("/weather/details")
    @Operation(
            summary = "날씨탭 : 사용자가 설정한 지역의 상세 날씨 조회 API",
            description = "사용자가 자신의 프로필에 저장한 목적지(행정 구역)의 현재 상세 날씨 정보를 조회합니다."
    )
    public ResponseEntity<WeatherDetailResponse> getWeatherDetailsByDestination(@LoginUserId Long userId) {
        WeatherDetailResponse weatherDetailResponse = weatherService.getWeatherDetailsByDestination(userId);
        return ResponseEntity.ok(weatherDetailResponse);
    }

    /**
     * 사용자가 설정한 지역의 주간 날씨 조회
     */
    @GetMapping("/weather/weekly")
    @Operation(
            summary = "날씨탭 : 사용자가 설정한 지역의 주간 날씨 조회 API",
            description = "사용자가 저장한 목적지의 오늘부터 6일 후까지의 주간 날씨 예보를 조회합니다."
    )
    public ResponseEntity<List<WeeklyWeatherDto>> getWeeklyWeatherByDestination(@LoginUserId Long userId) {
        List<WeeklyWeatherDto> weeklyWeather = weatherService.getWeeklyWeatherByDestination(userId);
        return ResponseEntity.ok(weeklyWeather);
    }
}