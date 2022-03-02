package com.dhbw.tutorsystem.security.authentication.exception;

import com.dhbw.tutorsystem.exception.TSBaseException;
import com.dhbw.tutorsystem.exception.TSErrorCode;

import org.springframework.http.HttpStatus;

public class ResetPasswordAccountNotEnabledException extends TSBaseException {

    private static final TSErrorCode errorCode = TSErrorCode.RESET_PASSWORD_ACCOUNT_NOT_ENABLED;

    public ResetPasswordAccountNotEnabledException() {
        super(HttpStatus.BAD_REQUEST, errorCode, "User account has not yet been enabled.");
    }

    public ResetPasswordAccountNotEnabledException(String message) {
        super(HttpStatus.BAD_REQUEST, errorCode, message);
    }

}
