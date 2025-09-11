package com.example.runway.domain.weather.service;

import com.example.runway.domain.weather.config.WeatherConfig.ReverseGeocodingApiProperties;
import com.example.runway.domain.weather.exception.ApiCallFailedException;
import com.example.runway.domain.weather.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeocodingService {

    private final RestTemplate restTemplate;
    private final ReverseGeocodingApiProperties apiProperties;

    public record LatLon(double lat, double lon) {}

    public LatLon getCoordinates(String address) {
        URI uri = UriComponentsBuilder
                .fromUriString(apiProperties.apiUrl())
                .queryParam("service", "address")
                .queryParam("request", "getcoord")
                .queryParam("version", "2.0")
                .queryParam("crs", "epsg:4326")
                .queryParam("address", address)
                .queryParam("format", "json")
                .queryParam("type", "road") //도로명 주소로 검색
                .queryParam("key", apiProperties.apiKey())
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUri();

        try {
            Map<String, Object> responseMap = restTemplate.getForObject(uri, Map.class);
            return parseCoordinates(responseMap).orElseThrow(() -> {
                log.warn("주소로부터 좌표를 찾을 수 없습니다: {}", address);
                return DataNotFoundException.EXCEPTION;
            });
        } catch (Exception e) {
            log.error("지오코딩 API 호출에 실패했습니다. 주소: {}", address, e);
            throw ApiCallFailedException.EXCEPTION;
        }
    }

    private Optional<LatLon> parseCoordinates(Map<String, Object> responseMap) {
        try {
            Map<String, Object> response = (Map<String, Object>) responseMap.get("response");
            if (!"OK".equals(response.get("status"))) {
                log.warn("VWorld Geocoding API가 OK 상태를 반환하지 않았습니다. 응답: {}", response);
                return Optional.empty();
            }

            Map<String, Object> result = (Map<String, Object>) response.get("result");
            Map<String, Object> point = (Map<String, Object>) result.get("point");

            // V-World API는 경도(x)와 위도(y)를 문자열로 반환
            double lon = Double.parseDouble((String) point.get("x"));
            double lat = Double.parseDouble((String) point.get("y"));

            return Optional.of(new LatLon(lat, lon));

        } catch (Exception e) {
            log.warn("VWorld Geocoding API 응답 파싱에 실패했습니다. 응답: {}", responseMap, e);
            return Optional.empty();
        }
    }
}