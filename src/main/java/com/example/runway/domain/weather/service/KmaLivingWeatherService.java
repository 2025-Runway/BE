package com.example.runway.domain.weather.service;

import com.example.runway.domain.weather.config.WeatherConfig.KmaLivingWeatherApiProperties;
import com.example.runway.domain.weather.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class KmaLivingWeatherService {
    private final RestTemplate restTemplate;
    private final KmaLivingWeatherApiProperties apiProperties;

    public String fetchUvIndex(String areaCode) {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "06";
        URI uri = UriComponentsBuilder
                .fromUriString(apiProperties.baseUrl() + "/getUVIdx")
                .queryParam("serviceKey", apiProperties.serviceKey())
                .queryParam("pageNo", "1")
                .queryParam("numOfRows", "10")
                .queryParam("dataType", "JSON")
                .queryParam("areaNo", areaCode)
                .queryParam("time", today)
                .build(true).toUri();
        try {
            Map<String, Object> responseMap = restTemplate.getForObject(uri, Map.class);
            return parseUvIndex(responseMap).orElseThrow(() -> DataNotFoundException.EXCEPTION);
        } catch (Exception e) {
            log.error("Failed to fetch UV index for area code: {}", areaCode, e);
            throw DataNotFoundException.EXCEPTION;
        }
    }

    private Optional<String> parseUvIndex(Map<String, Object> responseMap) {
        try {
            Map<String, Object> response = (Map<String, Object>) responseMap.get("response");
            Map<String, Object> body = (Map<String, Object>) response.get("body");
            Map<String, Object> items = (Map<String, Object>) body.get("items");
            List<Map<String, Object>> itemList = (List<Map<String, Object>>) items.get("item");
            if (!itemList.isEmpty()) {
                return Optional.ofNullable((String) itemList.get(0).get("today"));
            }
        } catch (Exception e) {
            log.warn("Could not parse UV index from API response.", e);
        }
        return Optional.empty();
    }
}