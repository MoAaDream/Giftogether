package com.moadream.giftogether.global.exception;

import org.springframework.http.HttpStatus;

public interface ExceptionCode {

    //인터넷 상태를 알려주는 메서드
    HttpStatus getHttpStatus();

    //에러가 발생했을 때 어디서 발생했는지 확인할 수 있는 코드
    String getCode();

    //에러 메세
    String getMessage();

    String getLink();
}
