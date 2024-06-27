package com.moadream.giftogether.global.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;


@Getter
public class ResourceNotFoundException extends EntityNotFoundException {


    private final ExceptionCode exceptionCode;

    public ResourceNotFoundException(ExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
    }

}
