package com.example.runway.domain.weather.service;

import com.example.runway.domain.weather.config.WeatherConfig.KmaMidTermApiProperties;
import com.example.runway.domain.weather.exception.WeatherApiFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KmaMidTermWeatherService {

    private final RestTemplate restTemplate;
    private final KmaMidTermApiProperties apiProperties;

    public Map<String, Object> fetchTemperatures(String regId, String tmFc) {
        URI uri = createUri("getMidTa", regId, tmFc); // 기온 조회는 getMidTa
        return callApi(uri);
    }

    public Map<String, Object> fetchWeatherForecasts(String regId, String tmFc) {
        URI uri = createUri("getMidLandFcst", regId, tmFc); // 날씨 조회는 getMidLandFcst
        return callApi(uri);
    }

    private URI createUri(String endpoint, String regId, String tmFc) {
        return UriComponentsBuilder
                .fromUriString(apiProperties.baseUrl() + "/" + endpoint)
                .queryParam("serviceKey", apiProperties.serviceKey())
                .queryParam("pageNo", "1")
                .queryParam("numOfRows", "10")
                .queryParam("dataType", "JSON")
                .queryParam("regId", regId)
                .queryParam("tmFc", tmFc)
                .build(true).toUri();
    }

    private Map<String, Object> callApi(URI uri) {
        try {
            Map<String, Object> responseMap = restTemplate.getForObject(uri, Map.class);
            // API 응답 구조가 복잡하여 item 리스트의 첫번째 요소(Map)를 바로 반환

            Map<String, Object> response = (Map<String, Object>) responseMap.get("response");
            Map<String, Object> body = (Map<String, Object>) response.get("body");
            Map<String, Object> items = (Map<String, Object>) body.get("items");
            List<Map<String, Object>> itemList = (List<Map<String, Object>>) items.get("item");
            return itemList.get(0);
        } catch (Exception e) {
            log.error("Failed to fetch mid-term weather data.", e);
            throw WeatherApiFailedException.EXCEPTION;
        }
    }
}