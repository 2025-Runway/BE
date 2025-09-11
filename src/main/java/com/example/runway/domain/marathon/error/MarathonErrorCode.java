package com.example.runway.domain.marathon.error;

import com.example.runway.global.error.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MarathonErrorCode implements BaseErrorCode {
    NOT_FOUND_MARATHON(404, "MR001", "해당 마라톤을 찾을 수 없습니다.");

    private final Integer status;
    private final String code;
    private final String reason;

    @Override
    public com.example.runway.global.dto.ErrorReason getErrorReason() {
        return com.example.runway.global.dto.ErrorReason.of(this.status, this.code, this.reason);
    }
}
