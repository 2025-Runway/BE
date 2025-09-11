package com.example.runway.domain.marathon.error;

import com.example.runway.global.error.BaseErrorException;

public class NotFountMarathon extends BaseErrorException {
    public static final NotFountMarathon EXCEPTION = new NotFountMarathon();
    public NotFountMarathon() {
        super(MarathonErrorCode.NOT_FOUND_MARATHON);
    }
}
