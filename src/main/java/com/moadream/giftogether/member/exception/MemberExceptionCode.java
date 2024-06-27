package com.moadream.giftogether.member.exception;

import com.moadream.giftogether.global.exception.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MemberExceptionCode implements ExceptionCode {

    NOT_FOUND_SOCIAL_ID(HttpStatus.BAD_REQUEST, "M-E-001", "로그인 정보를 확인하지 못했습니다. 다시 로그인해주세요", "/login");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
    private final String link;
}
