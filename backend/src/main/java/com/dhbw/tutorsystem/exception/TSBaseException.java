package com.dhbw.tutorsystem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import lombok.Getter;

public abstract class TSBaseException extends ResponseStatusException {

    @Getter
    private final TSErrorCode errorCode;

    @Getter
    private String message;

    public TSBaseException(HttpStatus status, TSErrorCode errorCode, String message) {
        super(status);
        this.errorCode = errorCode;
        this.message = message;
    }

    public TSBaseException(HttpStatus status, TSErrorCode errorCode) {
        super(status);
        this.errorCode = errorCode;
    }

}
