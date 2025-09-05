package com.example.runway.domain.weather.exception;

import com.example.runway.global.error.BaseErrorException;

public class DataNotFoundException extends BaseErrorException {
    public static final DataNotFoundException EXCEPTION = new DataNotFoundException();

    private DataNotFoundException() {
        super(WeatherErrorCode.DATA_NOT_FOUND);
    }
}