package com.moadream.giftogether.wishlist.exception;

import com.moadream.giftogether.global.exception.ExceptionCode;
import lombok.Getter;

@Getter
public class WishListException extends RuntimeException {

    private final ExceptionCode exceptionCode;

    public WishListException(ExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
    }
}
