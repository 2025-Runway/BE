package com.example.runway.domain.weather.dto;

public record WeeklyWeatherDto(
        String date,
        String dayOfWeek,
        String weatherAm,
        String weatherPm,
        int tempMin,
        int tempMax,
        String airQuality
) {}