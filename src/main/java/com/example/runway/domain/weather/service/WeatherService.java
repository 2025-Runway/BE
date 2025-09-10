package com.example.runway.domain.weather.service;

import com.example.runway.domain.weather.dto.WeatherDetailResponse;
import com.example.runway.domain.weather.dto.WeatherResponseDto;
import com.example.runway.domain.weather.dto.WeeklyWeatherDto;
import com.example.runway.domain.weather.exception.DataNotFoundException;
import com.example.runway.domain.weather.service.CoordinateConversionService.TMCoordinate;
import com.example.runway.domain.weather.service.CoordinateConversionService.XYCoordinate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

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
    private final KmaMidTermRegionService kmaMidTermRegionService;
    private final KmaMidTermWeatherService kmaMidTermWeatherService;


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

        log.info("liveData = {}", liveData);
        log.info("forecastData = {}", forecastData);

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

        log.info("forecastData : {}", forecastData.toString());

        // 3. 데이터 조합 및 변환
        String tempValue = liveData.get("T1H");
        String windSpeedValue = liveData.get("WSD");
        String skyCode = forecastData.get("SKY");
        String ptyCode = forecastData.get("PTY");
        String fineDustGrade = airQualityData.get("pm10Grade1h");

        if (tempValue == null || windSpeedValue == null || skyCode == null || ptyCode == null || fineDustGrade == null) {
            log.info("tempValue :{}", tempValue);
            log.info("windSpeedValue :{}", windSpeedValue);
            log.info("skyCode :{}", skyCode);
            log.info("ptyCode :{}", ptyCode);
            log.info("fineDustGrade :{}", fineDustGrade);

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

    // 주간 날씨 메서드

    public List<WeeklyWeatherDto> getWeeklyWeather(double lat, double lon) {
        List<WeeklyWeatherDto> weeklyForecast = new ArrayList<>();

        // --- 1. 오늘 ~ 3일 후 (Days 0-3) 데이터 생성 ---
        XYCoordinate xy = coordinateConversionService.convertLatLonToXY(lat, lon);
        List<Map<String, Object>> shortTermData = kmaWeatherService.fetchShortTermForecastData(xy.x(), xy.y());
        weeklyForecast.addAll(parseShortTermForecast(shortTermData));

        // --- 2. 4일 후 ~ 6일 후 데이터 생성 ---
        String address = reverseGeocodingService.getAddress(lat, lon);
        String landRegId = kmaMidTermRegionService.getLandRegionId(address)
                .orElseThrow(() -> DataNotFoundException.EXCEPTION);
        String tempRegId = kmaMidTermRegionService.getTempRegionId(address)
                .orElseThrow(() -> DataNotFoundException.EXCEPTION);

        String tmFc = calculateTmFc();
        Map<String, Object> midTermTempData = kmaMidTermWeatherService.fetchTemperatures(tempRegId, tmFc);
        Map<String, Object> midTermWeatherData = kmaMidTermWeatherService.fetchWeatherForecasts(landRegId, tmFc);
        weeklyForecast.addAll(parseMidTermForecast(midTermTempData, midTermWeatherData, tmFc));

        return weeklyForecast;
    }

    private List<WeeklyWeatherDto> parseShortTermForecast(List<Map<String, Object>> shortTermData) {
        Map<String, List<Map<String, Object>>> groupedByDay = shortTermData.stream()
                .collect(Collectors.groupingBy(item -> (String) item.get("fcstDate")));

        List<WeeklyWeatherDto> shortTermForecast = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = 0; i <= 3; i++) { // ✨ 0(오늘)부터 3(글피)까지 반복
            LocalDate date = today.plusDays(i);
            String dateStr = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            List<Map<String, Object>> dayData = groupedByDay.get(dateStr);

            if (dayData == null) continue;

            int tempMin, tempMax;

            if (i == 0) { // '오늘'은 TMP에서 직접 계산
                tempMin = dayData.stream()
                        .filter(item -> "TMP".equals(item.get("category")))
                        .mapToInt(item -> (int) Double.parseDouble((String) item.get("fcstValue")))
                        .min().orElse(Integer.MAX_VALUE);
                tempMax = dayData.stream()
                        .filter(item -> "TMP".equals(item.get("category")))
                        .mapToInt(item -> (int) Double.parseDouble((String) item.get("fcstValue")))
                        .max().orElse(Integer.MIN_VALUE);
            } else { // '내일' 부터는 TMN/TMX 값 사용
                tempMin = dayData.stream()
                        .filter(item -> "TMN".equals(item.get("category")))
                        .mapToInt(item -> (int) Double.parseDouble((String) item.get("fcstValue")))
                        .findFirst().orElse(Integer.MAX_VALUE);
                tempMax = dayData.stream()
                        .filter(item -> "TMX".equals(item.get("category")))
                        .mapToInt(item -> (int) Double.parseDouble((String) item.get("fcstValue")))
                        .findFirst().orElse(Integer.MIN_VALUE);
            }

            String weatherAm = getWeatherStatusFromShortTerm(dayData, 9, 12);
            String weatherPm = getWeatherStatusFromShortTerm(dayData, 13, 18);

            shortTermForecast.add(new WeeklyWeatherDto(
                    date.toString(),
                    date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN),
                    weatherAm,
                    weatherPm,
                    tempMin,
                    tempMax
            ));
        }
        return shortTermForecast;
    }

    // 단기예보의 시간대별 날씨를 대표 날씨로 변환하는 헬퍼
    private String getWeatherStatusFromShortTerm(List<Map<String, Object>> dayData, int startHour, int endHour) {
        // 강수(PTY) 예보를 SKY(하늘상태)보다 우선적으로 확인
        Optional<String> precipitationOpt = dayData.stream()
                .filter(item -> "PTY".equals(item.get("category")) && !"0".equals(item.get("fcstValue")))
                .filter(item -> {
                    int hour = Integer.parseInt((String) item.get("fcstTime")) / 100;
                    return hour >= startHour && hour <= endHour;
                })
                .findFirst() // 시간대 중 가장 먼저 나타나는 강수 현상을 기준으로 함
                .map(item -> {
                    // 단기예보 PTY 코드: 없음(0), 비(1), 비/눈(2), 눈(3), 소나기(4)
                    return switch ((String) item.get("fcstValue")) {
                        case "1" -> "흐림+비";
                        case "2" -> "흐림+비/눈";
                        case "3" -> "흐림+눈";
                        case "4" -> "흐림+소나기";
                        default -> "흐림";
                    };
                });

        // 강수 예보가 있으면 그것을 반환, 없으면 하늘 상태로 날씨 결정
        return precipitationOpt.orElseGet(() -> dayData.stream()
                .filter(item -> "SKY".equals(item.get("category")))
                .filter(item -> {
                    int hour = Integer.parseInt((String) item.get("fcstTime")) / 100;
                    return hour >= startHour && hour <= endHour;
                })
                .collect(Collectors.groupingBy(item -> (String) item.get("fcstValue"), Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(entry -> convertSkyCodeToCondition(entry.getKey()))
                .orElse("맑음"));
    }


    private List<WeeklyWeatherDto> parseMidTermForecast(Map<String, Object> tempData, Map<String, Object> weatherData, String tmFc) {
        List<WeeklyWeatherDto> midTermForecast = new ArrayList<>();
        LocalDate today = LocalDate.now();

        int startDay = 4;

        // 6일 후까지의 데이터만 필요
        for (int i = startDay; i <= 6; i++) {
            LocalDate forecastDate = today.plusDays(i);
            Object tempMin = tempData.get("taMin" + i);
            Object tempMax = tempData.get("taMax" + i);
            Object weatherAm = weatherData.get("wf" + i + "Am");
            Object weatherPm = weatherData.get("wf" + i + "Pm");

            if (tempMin == null || tempMax == null || weatherAm == null || weatherPm == null) continue;

            midTermForecast.add(new WeeklyWeatherDto(
                    forecastDate.toString(),
                    forecastDate.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN),
                    (String) weatherAm,
                    (String) weatherPm,
                    ((Number) tempMin).intValue(),
                    ((Number) tempMax).intValue()
            ));
        }
        return midTermForecast;
    }



    private String calculateTmFc() {
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();
        String baseDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        if (hour < 6) {
            return now.minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "1800";
        } else if (hour < 18) {
            return baseDate + "0600";
        } else {
            return baseDate + "1800";
        }
    }
}