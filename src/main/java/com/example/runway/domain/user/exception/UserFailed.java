package com.example.runway.domain.user.exception;

import com.example.runway.global.error.BaseErrorException;

public class UserFailed extends BaseErrorException {
    public static final UserFailed Exception = new UserFailed();

    public UserFailed() {
        super(UserErrorCode.UserNotFound);
    }
}