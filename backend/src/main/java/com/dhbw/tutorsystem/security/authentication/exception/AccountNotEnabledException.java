package com.dhbw.tutorsystem.security.authentication.exception;

import com.dhbw.tutorsystem.exception.TSBaseException;
import com.dhbw.tutorsystem.exception.TSErrorCode;

import org.springframework.http.HttpStatus;

public class AccountNotEnabledException extends TSBaseException {

    private static final TSErrorCode errorCode = TSErrorCode.ACCOUNT_NOT_ENABLED;

    public AccountNotEnabledException() {
        super(HttpStatus.BAD_REQUEST, errorCode, "User account has not yet been enabled.");
    }

}
