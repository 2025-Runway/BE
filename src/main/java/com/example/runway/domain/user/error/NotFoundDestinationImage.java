package com.example.runway.domain.user.error;

import com.example.runway.global.error.BaseErrorException;

public class NotFoundDestinationImage extends BaseErrorException {
    public static final NotFoundDestinationImage EXCEPTION = new NotFoundDestinationImage();
    public NotFoundDestinationImage() {
        super(UserErrorCode.NOT_FOUND_DESTINATION_IMAGE);
    }
}
