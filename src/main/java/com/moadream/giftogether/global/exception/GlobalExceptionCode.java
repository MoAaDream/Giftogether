package com.moadream.giftogether.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Getter
@RequiredArgsConstructor
public enum GlobalExceptionCode implements ExceptionCode {

    SERVER_ERROR(INTERNAL_SERVER_ERROR, "G-S-001", "Internal Server Error", "/home"),
    SESSION_NOT_FOUND(HttpStatus.NOT_FOUND, "S-C-001", "세션을 찾을 수 없습니다. 다시 로그인 해주세요", "/login");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
    private final String link;

}
