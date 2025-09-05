package com.example.runway.domain.search.error;

import com.example.runway.global.dto.ErrorReason;
import com.example.runway.global.error.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SearchErrorCode implements BaseErrorCode {
    NO_SEARCH_RESULT(404, "SR001", "검색 결과가 없습니다."),
    EMPTY_KEYWORD(400, "SR004", "검색어를 입력해주세요."),
    NOT_FOUND_COURSE(400, "SR005", "해당 코스를 찾을 수 없습니다.");

    private final Integer status;
    private final String code;
    private final String reason;

    @Override
    public ErrorReason getErrorReason() {
        return ErrorReason.of(this.status, this.code, this.reason);
    }
}
