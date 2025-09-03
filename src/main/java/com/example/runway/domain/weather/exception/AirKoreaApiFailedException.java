package com.example.runway.domain.weather.exception;

import com.example.runway.global.error.BaseErrorException;

public class AirKoreaApiFailedException extends BaseErrorException {
    public static final BaseErrorException EXCEPTION = new AirKoreaApiFailedException();

    private AirKoreaApiFailedException() {
        super(WeatherErrorCode.AIR_KOREA_API_CALL_FAILED);
    }
}