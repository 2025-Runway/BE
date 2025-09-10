package com.example.runway.domain.weather.service;

import com.example.runway.domain.weather.config.WeatherConfig.KmaLivingWeatherApiProperties;
import com.example.runway.domain.weather.exception.ApiCallFailedException;
import com.example.runway.domain.weather.exception.DataNotFoundException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
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
    private final ObjectMapper objectMapper;

    public String fetchUvIndex(String areaCode) {
        String latestApiTime = getLatestApiTime();

        URI uri = UriComponentsBuilder
                .fromUriString(apiProperties.baseUrl() + "/getUVIdxV4")
                .queryParam("serviceKey", apiProperties.serviceKey())
                .queryParam("pageNo", "1")
                .queryParam("numOfRows", "10")
                .queryParam("dataType", "JSON")
                .queryParam("areaNo", areaCode)
                .queryParam("time", latestApiTime)
                .build(true)
                .toUri();

        try {
            // API 응답을 DTO 구조로 직접 변환
            UvApiResponse response = restTemplate.getForObject(uri, UvApiResponse.class);
            log.info("API Response for UV index (areaCode: {}): {}", areaCode, objectMapper.writeValueAsString(response));

            // DTO에서 현재 시간에 맞는 자외선 지수 값을 추출
            return extractCurrentUvIndex(response)
                    .orElseThrow(() -> {
                        log.error("Failed to parse or find a valid UV index in the API response. areaCode: {}", areaCode);
                        return DataNotFoundException.EXCEPTION;
                    });

        } catch (RestClientException e) {
            log.error("Failed to fetch UV index from API for area code: {}. URI: {}", areaCode, uri, e);
            throw ApiCallFailedException.EXCEPTION;
        } catch (Exception e) {
            log.error("An unexpected error occurred while processing UV index for area code: {}", areaCode, e);
            throw DataNotFoundException.EXCEPTION;
        }
    }

    /**
     * 파싱된 API 응답 객체에서 현재 시간에 가장 가까운 자외선 지수 값을 추출합니다.
     */
    private Optional<String> extractCurrentUvIndex(UvApiResponse response) {
        if (response == null || response.getResponse() == null || response.getResponse().getBody() == null ||
                response.getResponse().getBody().getItems() == null || response.getResponse().getBody().getItems().getItem() == null ||
                response.getResponse().getBody().getItems().getItem().isEmpty()) {
            return Optional.empty();
        }

        // 결과 목록의 첫 번째 항목을 가져옵니다.
        UvApiResponse.Item item = response.getResponse().getBody().getItems().getItem().get(0);

        // 현재 시간에 가장 가까운 시간 키(h0, h3, h6 등)를 찾습니다.
        int currentHour = LocalDateTime.now().getHour();
        int closestHour = (currentHour / 3) * 3;
        String hourKey = "h" + closestHour;

        // ObjectMapper를 사용해 객체를 Map으로 변환하여 키로 값을 쉽게 찾습니다.
        Map<String, String> valueMap = objectMapper.convertValue(item, Map.class);
        String uvIndex = valueMap.get(hourKey);

        // 값이 null이 아니고 비어있지 않은 경우에만 Optional로 감싸 반환합니다.
        return Optional.ofNullable(uvIndex).filter(s -> !s.isEmpty());
    }

    private String getLatestApiTime() {
        // 데이터 발행 지연을 고려하여 현재 시간에서 3시간을 뺍니다.
        LocalDateTime safeRequestTime = LocalDateTime.now().minusHours(3);

        // API 발표 시간에 맞게 가장 가까운 이전 3의 배수 시간으로 맞춥니다.
        int targetHour = (safeRequestTime.getHour() / 3) * 3;

        // API가 요구하는 형식(yyyyMMddHH)으로 포맷팅합니다.
        String dateString = safeRequestTime.toLocalDate().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String timeString = String.format("%02d", targetHour);

        return dateString + timeString;
    }

    // API의 중첩된 JSON 구조를 매핑하기 위한 static DTO 클래스들
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UvApiResponse {
        private Response response;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Response {
            private Header header;
            private Body body;
        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Header {
            private String resultCode;
            private String resultMsg;
        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Body {
            private Items items;
        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Items {
            private List<Item> item;
        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Item {
            private String h0, h3, h6, h9, h12, h15, h18, h21, h24, h27, h30, h33, h36,
                    h39, h42, h45, h48, h51, h54, h57, h60, h63, h66, h69, h72, h75;
        }
    }
}

