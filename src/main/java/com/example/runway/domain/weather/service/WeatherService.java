package com.example.runway.domain.weather.service;

import com.example.runway.domain.weather.dto.WeatherDetailResponse;
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
    private final ReverseGeocodingService reverseGeocodingService;
    private final KmaLivingWeatherService kmaLivingWeatherService;
    private final AreaCodeService areaCodeService;

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

    public WeatherDetailResponse getWeatherDetails(double lat, double lon) {
        // 1. 좌표 변환
        XYCoordinate xy = coordinateConversionService.convertLatLonToXY(lat, lon);
        TMCoordinate tm = coordinateConversionService.convertLatLonToTM(lat, lon);
        String location = reverseGeocodingService.getAddress(lat, lon);
        String areaCode = areaCodeService.getAreaCode(location).orElse("1100000000"); // 기본값: 서울

        // 2. 외부 API 병렬 또는 순차 호출
        Map<String, String> liveData = kmaWeatherService.fetchWeatherData("getUltraSrtNcst", xy.x(), xy.y());
        Map<String, String> forecastData = kmaWeatherService.fetchWeatherData("getUltraSrtFcst", xy.x(), xy.y());
        String stationName = airKoreaService.fetchNearestStationName(tm.x(), tm.y());
        Map<String, String> airQualityData = airKoreaService.fetchAirQualityData(stationName);
        String uvIndexValue = kmaLivingWeatherService.fetchUvIndex(areaCode);

        // 3. 데이터 조합 및 변환
        String tempValue = liveData.get("T1H");
        String windSpeedValue = liveData.get("WSD");
        String skyCode = forecastData.get("SKY");
        String ptyCode = forecastData.get("PTY");
        String fineDustGrade = airQualityData.get("pm10Grade1h");

        if (tempValue == null || windSpeedValue == null || skyCode == null || ptyCode == null || fineDustGrade == null) {
            throw DataNotFoundException.EXCEPTION;
        }

        double temperature = Double.parseDouble(tempValue);
        String windSpeed = windSpeedValue + "m/s";
        String weather = combineWeatherConditions(skyCode, ptyCode);
        String fineDust = convertGradeToText(fineDustGrade);
        String uvIndex = convertUvGradeToText(uvIndexValue);

        return new WeatherDetailResponse(location, temperature, weather, windSpeed, fineDust, uvIndex);
    }

    private String combineWeatherConditions(String skyCode, String ptyCode) {
        String precipitation = switch (ptyCode) {
            case "1", "2", "5", "6" -> "+비";
            case "3", "7" -> "+눈";
            default -> "";
        };
        String skyCondition = switch (skyCode) {
            case "1" -> "맑음";
            case "3" -> "구름많음";
            case "4" -> "흐림";
            default -> "정보 없음";
        };
        if (!precipitation.isEmpty()) {
            return "흐림" + precipitation;
        }
        return skyCondition + precipitation;
    }

    private String convertUvGradeToText(String uvValue) {
        if (uvValue == null) return "알 수 없음";
        try {
            int value = Integer.parseInt(uvValue);
            if (value <= 2) return "낮음";
            if (value <= 5) return "보통";
            if (value <= 7) return "높음";
            if (value <= 10) return "매우높음";
            return "위험";
        } catch (NumberFormatException e) {
            return "점검중";
        }
    }
}