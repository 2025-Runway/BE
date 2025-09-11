package com.example.runway.domain.weather.exception;

import com.example.runway.global.error.BaseErrorException;

public class InvalidDestinationException extends BaseErrorException {
    public static final InvalidDestinationException EXCEPTION = new InvalidDestinationException();


    private InvalidDestinationException() {
        super(WeatherErrorCode.InvalidDestination);
    }
}
