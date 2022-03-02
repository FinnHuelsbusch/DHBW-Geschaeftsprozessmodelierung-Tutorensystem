package com.dhbw.tutorsystem.security.authentication.exception;

import com.dhbw.tutorsystem.exception.TSBaseException;
import com.dhbw.tutorsystem.exception.TSErrorCode;

import org.springframework.http.HttpStatus;

public class EmailAlreadyExistsException extends TSBaseException {

    private static final TSErrorCode errorCode = TSErrorCode.EMAIL_ALREADY_EXISTS;

    public EmailAlreadyExistsException() {
        super(HttpStatus.BAD_REQUEST, errorCode, "Email already exists.");
    }

    public EmailAlreadyExistsException(String message) {
        super(HttpStatus.BAD_REQUEST, errorCode, message);
    }

}
