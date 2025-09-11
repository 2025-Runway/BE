package com.example.runway.domain.weather.exception;

import com.example.runway.global.dto.ErrorReason;
import com.example.runway.global.error.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Getter
@AllArgsConstructor
public enum WeatherErrorCode implements BaseErrorCode {
    // API 연동 실패
    WEATHER_API_CALL_FAILED(INTERNAL_SERVER_ERROR, "WEATHER_500_1", "외부 날씨 API 호출에 실패했습니다."),
    AIR_KOREA_API_CALL_FAILED(INTERNAL_SERVER_ERROR, "WEATHER_500_2", "외부 대기질 API 호출에 실패했습니다."),
    // 데이터 조회 실패
    STATION_NOT_FOUND(NOT_FOUND, "WEATHER_404_1", "근처 측정소를 찾을 수 없습니다."),
    DATA_NOT_FOUND(INTERNAL_SERVER_ERROR, "WEATHER_500_3", "필수 응답 데이터가 누락되었습니다."),
    ApiCallFailed(INTERNAL_SERVER_ERROR,"WEATHER_500_4","기상 API 호출하는데 문제가 생겼습니다."),
    InvalidDestination(INTERNAL_SERVER_ERROR,"WEATHER_500_5","유효하지 않은 지역입니다.");

    private final HttpStatus status;
    private final String code;
    private final String reason;

    @Override
    public ErrorReason getErrorReason() {
        return ErrorReason.of(status.value(), code, reason);
    }
}