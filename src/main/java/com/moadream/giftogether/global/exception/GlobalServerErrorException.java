package com.moadream.giftogether.global.exception;

import lombok.Getter;

import static com.moadream.giftogether.global.exception.GlobalExceptionCode.SERVER_ERROR;

@Getter
public class GlobalServerErrorException extends RuntimeException {

    private final ExceptionCode exceptionCode;

    public GlobalServerErrorException() {
        super(SERVER_ERROR.getMessage());
        this.exceptionCode = GlobalExceptionCode.SERVER_ERROR;
    }

}
