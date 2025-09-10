package com.example.runway.domain.weather.exception;

import com.example.runway.global.error.BaseErrorException;

public class ApiCallFailedException extends BaseErrorException {
    public static final ApiCallFailedException EXCEPTION = new ApiCallFailedException();


    private ApiCallFailedException() {
        super(WeatherErrorCode.ApiCallFailed);
    }
}
