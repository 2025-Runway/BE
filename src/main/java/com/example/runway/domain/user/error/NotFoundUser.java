package com.example.runway.domain.user.error;

import com.example.runway.global.error.BaseErrorException;

public class NotFoundUser extends BaseErrorException {
    public static final NotFoundUser EXCEPTION = new NotFoundUser();
    public NotFoundUser() {
        super(UserErrorCode.NOT_FOUND_USER);
    }
}
