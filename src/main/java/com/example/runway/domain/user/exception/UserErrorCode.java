package com.example.runway.domain.user.exception;

import com.example.runway.global.dto.ErrorReason;
import com.example.runway.global.error.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements BaseErrorCode {
    UserNotFound(NOT_FOUND, "User_404_1", "사용자를 찾지 못하였습니다.");

    private HttpStatus status;
    private String code;
    private String reason;

    @Override
    public ErrorReason getErrorReason() {
        return ErrorReason.of(status.value(), code, reason);
    }
}