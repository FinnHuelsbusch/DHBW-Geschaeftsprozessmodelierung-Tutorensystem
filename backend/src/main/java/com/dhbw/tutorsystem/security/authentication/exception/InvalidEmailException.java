package com.dhbw.tutorsystem.security.authentication.exception;

import com.dhbw.tutorsystem.exception.TSBaseException;
import com.dhbw.tutorsystem.exception.TSErrorCode;

import org.springframework.http.HttpStatus;

public class InvalidEmailException extends TSBaseException {

    private static final TSErrorCode errorCode = TSErrorCode.INVALID_EMAIL;

    public InvalidEmailException() {
        super(HttpStatus.BAD_REQUEST, errorCode, "Email address is not valid.");
    }

    public InvalidEmailException(String message) {
        super(HttpStatus.BAD_REQUEST, errorCode, message);
    }

}
