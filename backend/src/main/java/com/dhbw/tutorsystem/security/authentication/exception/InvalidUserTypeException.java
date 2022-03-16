package com.dhbw.tutorsystem.security.authentication.exception;

import com.dhbw.tutorsystem.exception.TSBaseException;
import com.dhbw.tutorsystem.exception.TSErrorCode;

import org.springframework.http.HttpStatus;

public class InvalidUserTypeException extends TSBaseException {
    private static final TSErrorCode errorCode = TSErrorCode.INVALID_USER_TYPE;

    public InvalidUserTypeException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, errorCode, "User must be a student or a director.");
    }
}
