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
import java.time.LocalDateTime;
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
        // 각 API 엔드포인트에 맞는 조회 가능 시간을 계산
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
                        (existingValue, newValue) -> existingValue // 중복 키 발생 시 기존 값 유지
                ));
    }

    private List<Map<String, Object>> extractItems(Map<String, Object> responseMap) {
        if (responseMap == null || !responseMap.containsKey("response")) {
            log.warn("API response is empty or invalid.");
            return Collections.emptyList();
        }
        Map<String, Object> response = (Map<String, Object>) responseMap.get("response");
        if (response.get("body") == null) {
            Map<String, Object> header = (Map<String, Object>) response.get("header");
            if (header != null) {
                log.warn("API call failed with header: {}", header);
            }
            return Collections.emptyList();
        }
        Map<String, Object> body = (Map<String, Object>) response.get("body");
        if (body.get("items") == null) {
            log.warn("API response body contains no items field.");
            return Collections.emptyList();
        }
        // "items"가 비어있는 경우 빈 문자열("")로 올 수 있음
        if (!(body.get("items") instanceof Map)) {
            log.warn("API response 'items' field is not a Map or is empty.");
            return Collections.emptyList();
        }
        Map<String, Object> items = (Map<String, Object>) body.get("items");
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

    /**
     * 초단기실황, 초단기예보 API의 데이터 발표 시간을 고려하여
     * 조회 가능한 가장 최신의 base_date와 base_time을 계산합니다.
     */
    private BaseDateTime getBaseDateTime(String endpoint) {
        LocalDateTime now = LocalDateTime.now();

        // 각 API의 데이터 발표 시간(분)
        int cutoffMinute = "getUltraSrtNcst".equals(endpoint) ? 40 : 45;

        LocalDateTime baseDateTime;
        // 현재 분이 발표 시간(40분/45분) 이전이면, 1시간 전 데이터를 요청해야 함
        if (now.getMinute() < cutoffMinute) {
            baseDateTime = now.minusHours(1);
        } else {
            baseDateTime = now;
        }

        String baseDate = baseDateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String baseTime;

        if ("getUltraSrtNcst".equals(endpoint)) {
            // 초단기실황은 HH00 형식
            baseTime = baseDateTime.format(DateTimeFormatter.ofPattern("HH00"));
        } else { // "getUltraSrtFcst"
            // 초단기예보는 HH30 형식
            baseTime = baseDateTime.format(DateTimeFormatter.ofPattern("HH30"));
        }

        return new BaseDateTime(baseDate, baseTime);
    }

    public List<Map<String, Object>> fetchShortTermForecastData(String nx, String ny) {
        BaseDateTime dateTime = getBaseDateTimeForShortTerm();
        URI uri = UriComponentsBuilder
                .fromUriString(weatherApiProperties.baseUrl() + "/getVilageFcst")
                .queryParam("serviceKey", weatherApiProperties.serviceKey())
                .queryParam("numOfRows", "1000") // 3일치 데이터를 모두 받기 위해 넉넉하게 설정
                .queryParam("pageNo", "1")
                .queryParam("dataType", "JSON")
                .queryParam("base_date", dateTime.baseDate)
                .queryParam("base_time", dateTime.baseTime)
                .queryParam("nx", nx)
                .queryParam("ny", ny)
                .build(true).toUri();
        try {
            Map<String, Object> responseMap = restTemplate.getForObject(uri, Map.class);
            return extractItems(responseMap);
        } catch (Exception e) {
            log.error("Failed to fetch short-term forecast data. The original error was:", e);
            throw WeatherApiFailedException.EXCEPTION;
        }
    }

    /**
     * 단기예보 API의 데이터 발표 시간을 고려하여
     * 조회 가능한 가장 최신의 base_date와 base_time을 계산합니다.
     */
    private BaseDateTime getBaseDateTimeForShortTerm() {
        LocalDateTime now = LocalDateTime.now();
        // 데이터는 매 3시간마다 10분에 발표 (02:10, 05:10, ...)
        // 안전하게 15분의 여유를 둠
        LocalDateTime effectiveTime = now.minusMinutes(15);

        int hour = effectiveTime.getHour();
        int baseHour;

        // 현재 시간(여유 시간 차감 후)을 기준으로 가장 가까운 과거의 발표 시간을 찾음
        if (hour < 2) baseHour = 23;
        else if (hour < 5) baseHour = 2;
        else if (hour < 8) baseHour = 5;
        else if (hour < 11) baseHour = 8;
        else if (hour < 14) baseHour = 11;
        else if (hour < 17) baseHour = 14;
        else if (hour < 20) baseHour = 17;
        else if (hour < 23) baseHour = 20;
        else baseHour = 23;

        LocalDate baseDate = effectiveTime.toLocalDate();
        // 만약 현재 시간이 새벽 2시 이전이라 전날 23시 데이터를 써야 할 경우, 날짜도 하루 빼줌
        if (hour < 2) {
            baseDate = baseDate.minusDays(1);
        }

        return new BaseDateTime(baseDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")), String.format("%02d00", baseHour));
    }
}
