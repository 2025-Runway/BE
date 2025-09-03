package com.example.runway.domain.weather.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WeatherConfig {

    @Value("${kma.api.serviceKey}")
    private String serviceKey;

    @Value("${kma.api.baseUrl}")
    private String baseUrl;

    @Value("${airkorea.api.stationUrl}")
    private String airKoreaStationUrl;
    @Value("${airkorea.api.dataUrl}")
    private String airKoreaDataUrl;

    public record AirKoreaApiProperties(String stationUrl, String dataUrl, String serviceKey) {}

    @Bean
    public AirKoreaApiProperties airKoreaApiProperties() {
        return new AirKoreaApiProperties(airKoreaStationUrl, airKoreaDataUrl,serviceKey);
    }

    public record WeatherApiProperties(String serviceKey, String baseUrl) {}

    @Bean
    public WeatherApiProperties weatherApiProperties() {
        return new WeatherApiProperties(serviceKey, baseUrl);
    }
}