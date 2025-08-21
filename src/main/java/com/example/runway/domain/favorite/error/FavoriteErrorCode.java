package com.example.runway.domain.favorite.error;

import com.example.runway.global.dto.ErrorReason;
import com.example.runway.global.error.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.CONFLICT;


@Getter
@AllArgsConstructor
public enum FavoriteErrorCode implements BaseErrorCode {

    FavoriteDuplicate(CONFLICT,"Favorite_409_1","이미 찜한 코스입니다.");


    private HttpStatus status;
    private String code;
    private String reason;

    @Override
    public ErrorReason getErrorReason() {
        return ErrorReason.of(status.value(), code, reason);
    }
}
