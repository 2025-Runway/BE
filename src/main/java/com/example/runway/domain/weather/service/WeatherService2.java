package com.example.runway.domain.weather.service;

import com.example.runway.domain.user.entity.User;
import com.example.runway.domain.user.service.UserService;
import com.example.runway.domain.weather.dto.WeatherDetailResponse;
import com.example.runway.domain.weather.dto.WeatherResponseDto;
import com.example.runway.domain.weather.dto.WeeklyWeatherDto;
import com.example.runway.domain.weather.exception.DataNotFoundException;
import com.example.runway.domain.weather.exception.InvalidDestinationException;
import com.example.runway.domain.weather.service.CoordinateConversionService.TMCoordinate;
import com.example.runway.domain.weather.service.CoordinateConversionService.XYCoordinate;
import com.example.runway.domain.weather.service.GeocodingService.LatLon;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherService2 {

    private final CoordinateConversionService coordinateConversionService;
    private final KmaWeatherService kmaWeatherService;
    private final AirKoreaService airKoreaService;
    private final ReverseGeocodingService reverseGeocodingService;
    private final KmaLivingWeatherService kmaLivingWeatherService;
    private final AreaCodeService areaCodeService;
    private final KmaMidTermRegionService kmaMidTermRegionService;
    private final KmaMidTermWeatherService kmaMidTermWeatherService;
    private final GeocodingService geocodingService;
    private final UserService userService;

    public WeatherResponseDto getWeather(double lat, double lon) {
        XYCoordinate xy = coordinateConversionService.convertLatLonToXY(lat, lon);
        TMCoordinate tm = coordinateConversionService.convertLatLonToTM(lat, lon);
        Map<String, String> liveData = kmaWeatherService.fetchWeatherData("getUltraSrtNcst", xy.x(), xy.y());
        Map<String, String> forecastData = kmaWeatherService.fetchWeatherData("getUltraSrtFcst", xy.x(), xy.y());
        String stationName = airKoreaService.fetchNearestStationName(tm.x(), tm.y());
        Map<String, String> airQualityData = airKoreaService.fetchAirQualityData(stationName);

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
        XYCoordinate xy = coordinateConversionService.convertLatLonToXY(lat, lon);
        TMCoordinate tm = coordinateConversionService.convertLatLonToTM(lat, lon);
        String location = reverseGeocodingService.getAddress(lat, lon);
        String areaCode = areaCodeService.getAreaCode(location).orElse("1100000000");

        Map<String, String> liveData = kmaWeatherService.fetchWeatherData("getUltraSrtNcst", xy.x(), xy.y());
        Map<String, String> forecastData = kmaWeatherService.fetchWeatherData("getUltraSrtFcst", xy.x(), xy.y());
        String stationName = airKoreaService.fetchNearestStationName(tm.x(), tm.y());
        Map<String, String> airQualityData = airKoreaService.fetchAirQualityData(stationName);
        String uvIndexValue = kmaLivingWeatherService.fetchUvIndex(areaCode);

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
        String skyCondition = convertSkyCodeToCondition(skyCode);
        return skyCondition.equals("흐림") || !precipitation.isEmpty() ? "흐림" + precipitation : skyCondition;
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

    public List<WeeklyWeatherDto> getWeeklyWeather(double lat, double lon) {
        String address = reverseGeocodingService.getAddress(lat, lon);
        Map<String, String> airQualityForecast = getAirQualityForecast(address);

        XYCoordinate xy = coordinateConversionService.convertLatLonToXY(lat, lon);
        List<Map<String, Object>> shortTermData = kmaWeatherService.fetchShortTermForecastData(xy.x(), xy.y());
        Map<String, List<Map<String, Object>>> shortTermGroupedByDay = shortTermData.stream()
                .collect(Collectors.groupingBy(item -> (String) item.get("fcstDate")));

        String landRegId = kmaMidTermRegionService.getLandRegionId(address).orElseThrow(()->DataNotFoundException.EXCEPTION);
        String tempRegId = kmaMidTermRegionService.getTempRegionId(address).orElseThrow(()->DataNotFoundException.EXCEPTION);
        String tmFc = calculateTmFc();
        Map<String, Object> midTermTempData = kmaMidTermWeatherService.fetchTemperatures(tempRegId, tmFc);
        Map<String, Object> midTermWeatherData = kmaMidTermWeatherService.fetchWeatherForecasts(landRegId, tmFc);



        List<WeeklyWeatherDto> weeklyForecast = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = 0; i < 7; i++) {
            LocalDate date = today.plusDays(i);
            String dateStrYyyyMmDd = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String dateStrYyyy_Mm_Dd = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String dayOfWeek = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN);
            String airQuality = airQualityForecast.getOrDefault(dateStrYyyy_Mm_Dd, "높음").equals("높음") ? "나쁨" : "좋음";


            String weatherAm, weatherPm;
            int tempMin, tempMax;

            if (i < 3) { // 오늘, 내일, 모레: 단기 예보 사용
                List<Map<String, Object>> dayData = shortTermGroupedByDay.get(dateStrYyyyMmDd);
                if (dayData == null) continue;

                final boolean isToday = (i == 0);
                tempMin = dayData.stream()
                        .filter(item -> "TMN".equals(item.get("category")) || (isToday && "TMP".equals(item.get("category"))))
                        .mapToInt(item -> (int) Double.parseDouble((String) item.get("fcstValue")))
                        .min().orElse(Integer.MAX_VALUE);
                tempMax = dayData.stream()
                        .filter(item -> "TMX".equals(item.get("category")) || (isToday && "TMP".equals(item.get("category"))))
                        .mapToInt(item -> (int) Double.parseDouble((String) item.get("fcstValue")))
                        .max().orElse(Integer.MIN_VALUE);

                weatherAm = getWeatherStatusFromShortTerm(dayData, 9, 12);
                weatherPm = getWeatherStatusFromShortTerm(dayData, 13, 18);
            } else { // 3일 후부터: 중기 예보 사용
                Object tempMinObj = midTermTempData.get("taMin" + (i+1));
                Object tempMaxObj = midTermTempData.get("taMax" + (i+1) );
                Object weatherAmObj = midTermWeatherData.get("wf" + (i+1) + "Am");
                Object weatherPmObj = midTermWeatherData.get("wf" + (i+1) + "Pm");



                if (tempMinObj == null || tempMaxObj == null || weatherAmObj == null || weatherPmObj == null) continue;

                tempMin = ((Number) tempMinObj).intValue();
                tempMax = ((Number) tempMaxObj).intValue();
                weatherAm = (String) weatherAmObj;
                weatherPm = (String) weatherPmObj;
            }





            weeklyForecast.add(new WeeklyWeatherDto(date.toString(), dayOfWeek, weatherAm, weatherPm, tempMin, tempMax, airQuality));
        }
        return weeklyForecast;
    }

    private Map<String, String> getAirQualityForecast(String address) {
        Map<String, String> forecast = new HashMap<>();
        String region = getRegionForAirForecast(address);

        List<Map<String, Object>> shortTerm = airKoreaService.fetchMinuDustFrcstDspth();
        for (Map<String, Object> item : shortTerm) {
            String date = (String) item.get("informData");
            String gradeStr = (String) item.get("informGrade");
            forecast.put(date, parseAirQualityGrade(gradeStr, region));
        }


        List<Map<String, Object>> weekly = airKoreaService.fetchMinuDustWeekFrcstDspth();

        if (weekly != null && !weekly.isEmpty()) {
            Map<String, Object> item = weekly.get(0);
            forecast.put((String) item.get("frcstOneDt"), parseAirQualityGrade((String) item.get("frcstOneCn"), region));
            forecast.put((String) item.get("frcstTwoDt"), parseAirQualityGrade((String) item.get("frcstTwoCn"), region));
            forecast.put((String) item.get("frcstThreeDt"), parseAirQualityGrade((String) item.get("frcstThreeCn"), region));
            forecast.put((String) item.get("frcstFourDt"), parseAirQualityGrade((String) item.get("frcstFourCn"), region));
        }

        log.info("Air quality forecast: {}", forecast);


        return forecast;
    }

    private String getRegionForAirForecast(String address) {
        if (address == null) return "서울";
        if (address.startsWith("서울")) return "서울";
        if (address.startsWith("인천")) return "인천";
        if (address.startsWith("경기")) return "경기남부";
        if (address.startsWith("강원")) return "영서";
        if (address.startsWith("대전")) return "대전";
        if (address.startsWith("세종")) return "세종";
        if (address.startsWith("충남")) return "충남";
        if (address.startsWith("충북")) return "충북";
        if (address.startsWith("광주")) return "광주";
        if (address.startsWith("전북")) return "전북";
        if (address.startsWith("전남")) return "전남";
        if (address.startsWith("부산")) return "부산";
        if (address.startsWith("대구")) return "대구";
        if (address.startsWith("울산")) return "울산";
        if (address.startsWith("경북")) return "경북";
        if (address.startsWith("경남")) return "경남";
        if (address.startsWith("제주")) return "제주";
        return "서울";
    }

    private String parseAirQualityGrade(String gradeStr, String region) {
        if (gradeStr == null || region == null) return "정보 없음";
        Pattern pattern = Pattern.compile(region + "\\s*:\\s*([^,]+)");
        Matcher matcher = pattern.matcher(gradeStr);
        return matcher.find() ? matcher.group(1).trim() : "정보 없음";
    }

    private String getWeatherStatusFromShortTerm(List<Map<String, Object>> dayData, int startHour, int endHour) {
        Optional<String> precipitationOpt = dayData.stream()
                .filter(item -> "PTY".equals(item.get("category")) && !"0".equals(item.get("fcstValue")))
                .filter(item -> { int hour = Integer.parseInt((String) item.get("fcstTime")) / 100; return hour >= startHour && hour <= endHour; })
                .findFirst()
                .map(item -> switch ((String) item.get("fcstValue")) {
                    case "1", "2", "4" -> "흐림+비";
                    case "3" -> "흐림+눈";
                    default -> "흐림";
                });

        return precipitationOpt.orElseGet(() -> dayData.stream()
                .filter(item -> "SKY".equals(item.get("category")))
                .filter(item -> { int hour = Integer.parseInt((String) item.get("fcstTime")) / 100; return hour >= startHour && hour <= endHour; })
                .collect(Collectors.groupingBy(item -> (String) item.get("fcstValue"), Collectors.counting()))
                .entrySet().stream().max(Map.Entry.comparingByValue())
                .map(entry -> convertSkyCodeToCondition(entry.getKey())).orElse("맑음"));
    }

    private String calculateTmFc() {
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();
        String baseDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        if (hour < 6) return now.minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "0600";
        if (hour < 18) return baseDate + "0600";
        return baseDate + "1800";
    }

    public WeatherDetailResponse getWeatherDetailsByDestination(Long userId) {
        User user = userService.getUser(userId);
        String destination = user.getDestination();
        if (destination == null || destination.isBlank()) throw InvalidDestinationException.EXCEPTION;
        LatLon coords = geocodingService.getCoordinates(destination);
        return getWeatherDetails(coords.lat(), coords.lon());
    }

    public List<WeeklyWeatherDto> getWeeklyWeatherByDestination(Long userId) {
        User user = userService.getUser(userId);
        String destination = user.getDestination();
        if (destination == null || destination.isBlank()) throw InvalidDestinationException.EXCEPTION;
        LatLon coords = geocodingService.getCoordinates(destination);
        return getWeeklyWeather(coords.lat(), coords.lon());
    }
}