package com.moadream.giftogether.member.exception;

import com.moadream.giftogether.global.exception.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MemberExceptionCode implements ExceptionCode {

    NOT_FOUND_SOCIAL_ID(HttpStatus.BAD_REQUEST, "M-E-001", "로그인 정보를 확인하지 못했습니다. 다시 로그인해주세요", "/login"),
    NO_AUTHORIZE_ADMIN(HttpStatus.UNAUTHORIZED, "M-E-002", "어드민이 아닙니다.", "/home"),
    CHECK_BLACKLIST(HttpStatus.BAD_REQUEST, "M-E-003", "부적절한 사용자입니다. 사용할 수 없습니다.", "/home");



    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
    private final String link;
}
