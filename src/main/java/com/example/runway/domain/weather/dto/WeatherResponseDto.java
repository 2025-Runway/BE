package com.example.runway.domain.weather.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WeatherResponseDto {
    private double temperature;     // 기온
    private String condition;       // 날씨 상태 (예: "맑음")
    private String airQuality;      // 대기질 (예: "보통")
}
