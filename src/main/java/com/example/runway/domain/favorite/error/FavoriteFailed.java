package com.example.runway.domain.favorite.error;

import com.example.runway.global.error.BaseErrorException;

public class FavoriteFailed extends BaseErrorException {

    public static final FavoriteFailed Exception = new FavoriteFailed();


    public FavoriteFailed() {
        super(FavoriteErrorCode.FavoriteDuplicate);
    }
}
