package com.example.runway.infra.s3.exception;

import com.example.runway.global.dto.ErrorReason;
import com.example.runway.global.error.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Getter
@AllArgsConstructor
public enum S3ErrorCode implements BaseErrorCode {
    S3_UPLOAD_FAILED(INTERNAL_SERVER_ERROR, "S3_500_1", "파일 업로드에 실패했습니다.");

    private HttpStatus status;
    private String code;
    private String reason;

    @Override
    public ErrorReason getErrorReason() {
        return ErrorReason.of(status.value(), code, reason);
    }
}
