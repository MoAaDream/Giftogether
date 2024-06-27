package com.moadream.giftogether.member.exception;

import com.moadream.giftogether.global.exception.ExceptionCode;
import lombok.Getter;

@Getter
public class MemberException extends RuntimeException {

    private final ExceptionCode exceptionCode;

    public MemberException(ExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
    }
}
