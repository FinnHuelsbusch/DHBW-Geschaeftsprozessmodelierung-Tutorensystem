package com.dhbw.tutorsystem.security.authentication.exception;

import com.dhbw.tutorsystem.exception.TSBaseException;
import com.dhbw.tutorsystem.exception.TSErrorCode;

import org.springframework.http.HttpStatus;

public class UserAlreadyEnabledException extends TSBaseException {

    private static final TSErrorCode errorCode = TSErrorCode.USER_ALREADY_ENABLED;

    public UserAlreadyEnabledException() {
        super(HttpStatus.BAD_REQUEST, errorCode, "User is already enabled.");
    }

    public UserAlreadyEnabledException(String message) {
        super(HttpStatus.BAD_REQUEST, errorCode, message);
    }

}
