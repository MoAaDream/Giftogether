package com.moadream.giftogether.global.exception;

import lombok.Getter;

@Getter
public class SessionNotFoundException extends RuntimeException {

    private final ExceptionCode exceptionCode;

    public SessionNotFoundException(ExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
    }
}
