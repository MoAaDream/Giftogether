package com.moadream.giftogether.wishlist.exception;

import com.moadream.giftogether.global.exception.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum WishlistExceptionCode implements ExceptionCode {

    NOT_MY_WISHLIST(HttpStatus.BAD_REQUEST, "W-E-001", "나의 위시리스트가 아닙니다.", "/wishlist/my/0"),
    NOT_FOUND_WISHLIST(HttpStatus.BAD_REQUEST, "W-E-002", "위시리스트 링크가 잘못되었습니다.", "/wishlist/my/0"),
    NOT_DELETE_WISHLIST_BY_FUNDING(HttpStatus.BAD_REQUEST, "W-E-003", "모금이 시작되어 삭제할 수 없습니다.", "/wishlist/my/0"),
    NOT_DEADLINE_AFTER(HttpStatus.BAD_REQUEST, "W-E-004", "기간이 지나지않아 삭제할 수 없습니다.", "/wishlist/my/0");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
    private final String link;


}
