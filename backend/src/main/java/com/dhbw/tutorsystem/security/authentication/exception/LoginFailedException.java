package com.dhbw.tutorsystem.security.authentication.exception;

import com.dhbw.tutorsystem.exception.TSBaseException;
import com.dhbw.tutorsystem.exception.TSErrorCode;

import org.springframework.http.HttpStatus;

public class LoginFailedException extends TSBaseException {

    private static final TSErrorCode errorCode = TSErrorCode.LOGIN_FAILED;

    public LoginFailedException() {
        super(HttpStatus.BAD_REQUEST, errorCode, "Login failed.");
    }

}
