package com.example.runway.domain.weather.exception;

import com.example.runway.global.error.BaseErrorException;

public class StationNotFoundException extends BaseErrorException {
    public static final StationNotFoundException EXCEPTION = new StationNotFoundException();

    private StationNotFoundException() {
        super(WeatherErrorCode.STATION_NOT_FOUND);
    }
}