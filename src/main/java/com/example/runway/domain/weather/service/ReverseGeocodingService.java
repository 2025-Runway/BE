package com.example.runway.domain.weather.service;

import com.example.runway.domain.weather.config.WeatherConfig.ReverseGeocodingApiProperties;
import com.example.runway.domain.weather.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReverseGeocodingService {

    private final RestTemplate restTemplate;
    private final ReverseGeocodingApiProperties apiProperties;

    public String getAddress(double lat, double lon) {
        URI uri = UriComponentsBuilder
                .fromUriString(apiProperties.apiUrl())
                .queryParam("service", "address")
                .queryParam("request", "getAddress")
                .queryParam("key", apiProperties.apiKey())
                .queryParam("point", lon + "," + lat)
                .queryParam("type", "ROAD")
                .queryParam("format", "json")
                .queryParam("crs", "epsg:4326")
                .build(true).toUri();
        try {
            Map<String, Object> responseMap = restTemplate.getForObject(uri, Map.class);
            return parseAddress(responseMap).orElseThrow(() -> DataNotFoundException.EXCEPTION);
        } catch (Exception e) {
            log.error("Failed to fetch address from geocoding API.", e);
            throw DataNotFoundException.EXCEPTION;
        }
    }

    private Optional<String> parseAddress(Map<String, Object> responseMap) {
        try {
            Map<String, Object> response = (Map<String, Object>) responseMap.get("response");
            String status = (String) response.get("status");

            if (!"OK".equals(status)) {
                log.warn("VWorld API did not return OK status. Response: {}", response);
                return Optional.empty();
            }

            List<Map<String, Object>> resultList = (List<Map<String, Object>>) response.get("result");

            if (resultList != null && !resultList.isEmpty()) {
                Map<String, Object> firstResult = resultList.get(0);
                Map<String, Object> structure = (Map<String, Object>) firstResult.get("structure");

                if (structure != null) {
                    String level1 = (String) structure.get("level1");
                    String level2 = (String) structure.get("level2");
                    return Optional.of(level1 + " " + level2);
                }
            }
        } catch (Exception e) {
            log.warn("Could not parse address from VWorld API response. Response: {}", responseMap, e);
        }
        return Optional.empty();
    }
}