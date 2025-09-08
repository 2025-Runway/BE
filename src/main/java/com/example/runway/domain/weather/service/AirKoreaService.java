package com.example.runway.domain.weather.service;

import com.example.runway.domain.weather.config.WeatherConfig.AirKoreaApiProperties;
import com.example.runway.domain.weather.config.WeatherConfig.WeatherApiProperties;
import com.example.runway.domain.weather.exception.AirKoreaApiFailedException;
import com.example.runway.domain.weather.exception.StationNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AirKoreaService {

    private final RestTemplate restTemplate;
    private final AirKoreaApiProperties airKoreaApiProperties;

    public String fetchNearestStationName(String tmX, String tmY) {
        URI uri = UriComponentsBuilder
                .fromUriString(airKoreaApiProperties.stationUrl() + "/getNearbyMsrstnList")
                .queryParam("serviceKey", airKoreaApiProperties.serviceKey())
                .queryParam("returnType", "json")
                .queryParam("tmX", tmX)
                .queryParam("tmY", tmY)
                .queryParam("ver", "1.1")
                .build(true).toUri();
        try {
            Map<String, Object> responseMap = restTemplate.getForObject(uri, Map.class);
            List<Map<String, Object>> items = extractItems(responseMap);
            if (items.isEmpty()) {
                throw StationNotFoundException.EXCEPTION;
            }
            return (String) items.get(0).get("stationName");
        } catch (Exception e) {
            log.error("Failed to fetch nearest station name.", e);
            throw AirKoreaApiFailedException.EXCEPTION;
        }
    }

    public Map<String, String> fetchAirQualityData(String stationName) {
        String encodedStationName = URLEncoder.encode(stationName, StandardCharsets.UTF_8);
        URI uri = UriComponentsBuilder
                .fromUriString(airKoreaApiProperties.dataUrl() + "/getMsrstnAcctoRltmMesureDnsty")
                .queryParam("serviceKey", airKoreaApiProperties.serviceKey())
                .queryParam("returnType", "json")
                .queryParam("numOfRows", "1")
                .queryParam("pageNo", "1")
                .queryParam("stationName", encodedStationName)
                .queryParam("dataTerm", "DAILY")
                .queryParam("ver", "1.3")
                .build(true).toUri();
        try {
            Map<String, Object> responseMap = restTemplate.getForObject(uri, Map.class);
            return parseAirKoreaData(responseMap);
        } catch (Exception e) {
            log.error("Failed to fetch air quality data for station: {}", stationName, e);
            throw AirKoreaApiFailedException.EXCEPTION;
        }
    }

    private Map<String, String> parseAirKoreaData(Map<String, Object> responseMap) {
        List<Map<String, Object>> itemList = extractItems(responseMap);
        if (itemList.isEmpty()) return Collections.emptyMap();
        return itemList.get(0).entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> String.valueOf(e.getValue())));
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
        if (itemsObject instanceof List) { // 에어코리아 응답은 item이 바로 List
            return (List<Map<String, Object>>) itemsObject;
        }
        log.warn("Could not parse 'items' field from AirKorea API response. Found: {}", itemsObject);
        return Collections.emptyList();
    }
}