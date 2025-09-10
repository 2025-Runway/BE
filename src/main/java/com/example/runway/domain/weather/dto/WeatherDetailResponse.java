package com.example.runway.domain.weather.dto;

public record WeatherDetailResponse(
        String location,
        double temperature,
        String weather,
        String windSpeed,
        String fineDust,
        String uvIndex
) {}