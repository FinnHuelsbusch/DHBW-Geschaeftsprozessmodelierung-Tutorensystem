package com.dhbw.tutorsystem.security.authentication.exception;

import com.dhbw.tutorsystem.exception.TSBaseException;
import com.dhbw.tutorsystem.exception.TSErrorCode;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends TSBaseException {

    private static final TSErrorCode errorCode = TSErrorCode.USER_NOT_FOUND;

    public UserNotFoundException() {
        super(HttpStatus.BAD_REQUEST, errorCode, "User could not be found.");
    }

    public UserNotFoundException(String message) {
        super(HttpStatus.BAD_REQUEST, errorCode, message);
    }

}
