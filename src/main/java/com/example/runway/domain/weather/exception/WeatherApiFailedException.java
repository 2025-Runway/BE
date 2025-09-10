package com.example.runway.domain.weather.exception;

import com.example.runway.global.error.BaseErrorException;

public class WeatherApiFailedException extends BaseErrorException {
    public static final BaseErrorException EXCEPTION = new WeatherApiFailedException();

    private WeatherApiFailedException() {
        super(WeatherErrorCode.WEATHER_API_CALL_FAILED);
    }
}