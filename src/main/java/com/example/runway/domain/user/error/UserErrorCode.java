package com.example.runway.domain.user.error;

import com.example.runway.global.dto.ErrorReason;
import com.example.runway.global.error.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements BaseErrorCode {
    NOT_FOUND_USER(404, "U001", "사용자를 찾을 수 없습니다."),
    NOT_FOUND_DESTINATION_IMAGE(404, "U002", "지역 이미지를 찾을 수 없습니다.");

    private final Integer code;
    private final String message;
    private final String reason;

    @Override
    public ErrorReason getErrorReason() {
        return ErrorReason.of(this.code, this.message, this.reason);
    }
}
