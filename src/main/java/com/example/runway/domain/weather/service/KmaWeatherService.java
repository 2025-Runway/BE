package com.example.runway.domain.weather.service;

import com.example.runway.domain.weather.config.WeatherConfig.WeatherApiProperties;
import com.example.runway.domain.weather.exception.WeatherApiFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class KmaWeatherService {

    private final RestTemplate restTemplate;
    private final WeatherApiProperties weatherApiProperties;

    private record BaseDateTime(String baseDate, String baseTime) {}

    public Map<String, String> fetchWeatherData(String endpoint, String nx, String ny) {
        BaseDateTime baseDateTime = getBaseDateTime(endpoint);

        URI uri = UriComponentsBuilder
                .fromUriString(weatherApiProperties.baseUrl() + "/" + endpoint)
                .queryParam("serviceKey", weatherApiProperties.serviceKey())
                .queryParam("numOfRows", "60")
                .queryParam("pageNo", "1")
                .queryParam("dataType", "JSON")
                .queryParam("base_date", baseDateTime.baseDate)
                .queryParam("base_time", baseDateTime.baseTime)
                .queryParam("nx", nx)
                .queryParam("ny", ny)
                .build(true)
                .toUri();

        try {
            Map<String, Object> responseMap = restTemplate.getForObject(uri, Map.class);
            return parseWeatherData(responseMap);
        } catch (Exception e) {
            log.error("Failed to fetch weather data from {}. The original error was:", endpoint, e);
            throw WeatherApiFailedException.EXCEPTION;
        }
    }

    private Map<String, String> parseWeatherData(Map<String, Object> responseMap) {
        List<Map<String, Object>> itemList = extractItems(responseMap);
        if (itemList.isEmpty()) return Collections.emptyMap();

        return itemList.stream()
                .collect(Collectors.toMap(
                        item -> (String) item.get("category"),
                        item -> Objects.toString(item.get("obsrValue"), (String) item.get("fcstValue")),
                        (existingValue, newValue) -> existingValue
                ));
    }

    private List<Map<String, Object>> extractItems(Map<String, Object> responseMap) {
        if (responseMap == null || !responseMap.containsKey("response")) {
            log.warn("API response is empty or invalid.");
            return Collections.emptyList();
        }
        Map<String, Object> response = (Map<String, Object>) responseMap.get("response");
        if (response == null || !response.containsKey("body")) {
            Map<String, Object> header = (Map<String, Object>) response.get("header");
            log.warn("API call failed with header: {}", header);
            return Collections.emptyList();
        }
        Map<String, Object> body = (Map<String, Object>) response.get("body");
        if (body == null || !body.containsKey("items")) {
            log.warn("API response body contains no items field.");
            return Collections.emptyList();
        }
        Object itemsObject = body.get("items");
        if (!(itemsObject instanceof Map)) {
            log.warn("API response 'items' field is not a Map: {}", itemsObject);
            return Collections.emptyList();
        }
        Map<String, Object> items = (Map<String, Object>) itemsObject;
        if (items.get("item") == null) {
            log.warn("API response body contains no item data.");
            return Collections.emptyList();
        }
        Object itemObject = items.get("item");
        if (itemObject instanceof List) {
            return (List<Map<String, Object>>) itemObject;
        } else if (itemObject instanceof Map) {
            return Collections.singletonList((Map<String, Object>) itemObject);
        }
        return Collections.emptyList();
    }

    private BaseDateTime getBaseDateTime(String endpoint) {
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        if ("getUltraSrtNcst".equals(endpoint)) {
            LocalTime baseTime = time.getMinute() < 10 ? time.minusHours(1) : time;
            return new BaseDateTime(date.format(dateFormatter), baseTime.format(DateTimeFormatter.ofPattern("HH00")));
        } else { // "getUltraSrtFcst"
            LocalTime baseTime = time.getMinute() < 45 ? time.minusHours(1) : time;
            return new BaseDateTime(date.format(dateFormatter), baseTime.format(DateTimeFormatter.ofPattern("HH30")));
        }
    }
}