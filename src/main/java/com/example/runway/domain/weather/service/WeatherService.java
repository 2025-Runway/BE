package com.example.runway.domain.weather.service;

import com.example.runway.domain.weather.dto.WeatherResponseDto;
import com.example.runway.domain.weather.exception.DataNotFoundException;
import com.example.runway.domain.weather.service.CoordinateConversionService.TMCoordinate;
import com.example.runway.domain.weather.service.CoordinateConversionService.XYCoordinate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherService {

    private final CoordinateConversionService coordinateConversionService;
    private final KmaWeatherService kmaWeatherService;
    private final AirKoreaService airKoreaService;

    /**
     * 각 전문 서비스를 조율하여 날씨와 미세먼지 정보를 최종 조합합니다.
     */
    public WeatherResponseDto getWeather(double lat, double lon) {
        // 1. 좌표 변환 서비스 호출
        XYCoordinate xy = coordinateConversionService.convertLatLonToXY(lat, lon);
        TMCoordinate tm = coordinateConversionService.convertLatLonToTM(lat, lon);

        // 2. 기상청 날씨 서비스 호출
        Map<String, String> liveData = kmaWeatherService.fetchWeatherData("getUltraSrtNcst", xy.x(), xy.y());
        Map<String, String> forecastData = kmaWeatherService.fetchWeatherData("getUltraSrtFcst", xy.x(), xy.y());

        // 3. 에어코리아 미세먼지 서비스 호출
        String stationName = airKoreaService.fetchNearestStationName(tm.x(), tm.y());
        Map<String, String> airQualityData = airKoreaService.fetchAirQualityData(stationName);


        // 4. 모든 데이터 조합
        String tempValue = liveData.get("T1H");
        double temperature = (tempValue != null) ? Double.parseDouble(tempValue) : 0.0;

        String skyCode = forecastData.get("SKY");
        String condition = convertSkyCodeToCondition(skyCode);

        String airQualityGrade = airQualityData.get("pm10Grade1h");
        String airQuality = convertGradeToText(airQualityGrade);

        if (tempValue == null || skyCode == null || airQualityGrade == null) {
            throw DataNotFoundException.EXCEPTION;
        }

        return new WeatherResponseDto(temperature, condition, airQuality);
    }

    private String convertSkyCodeToCondition(String skyCode) {
        if (skyCode == null) return "알 수 없음";
        return switch (skyCode) {
            case "1" -> "맑음";
            case "3" -> "구름많음";
            case "4" -> "흐림";
            default -> "정보 없음";
        };
    }

    private String convertGradeToText(String grade) {
        if (grade == null) return "알 수 없음";
        return switch (grade) {
            case "1" -> "좋음";
            case "2" -> "보통";
            case "3" -> "나쁨";
            case "4" -> "매우나쁨";
            default -> "점검중";
        };
    }
}