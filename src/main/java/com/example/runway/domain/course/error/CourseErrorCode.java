package com.example.runway.domain.course.error;

import com.example.runway.global.dto.ErrorReason;
import com.example.runway.global.error.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;


@Getter
@AllArgsConstructor
public enum CourseErrorCode implements BaseErrorCode {

    CourseNotFound(INTERNAL_SERVER_ERROR,"Course_500_1","코스를 찾지 못하였습니다.");


    private HttpStatus status;
    private String code;
    private String reason;

    @Override
    public ErrorReason getErrorReason() {
        return ErrorReason.of(status.value(), code, reason);
    }
}
