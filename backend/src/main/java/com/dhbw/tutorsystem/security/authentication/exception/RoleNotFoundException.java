package com.dhbw.tutorsystem.security.authentication.exception;

import com.dhbw.tutorsystem.exception.TSBaseException;
import com.dhbw.tutorsystem.exception.TSErrorCode;

import org.springframework.http.HttpStatus;

public class RoleNotFoundException extends TSBaseException {

    private static final TSErrorCode errorCode = TSErrorCode.ROLE_NOT_FOUND;

    public RoleNotFoundException() {
        super(HttpStatus.BAD_REQUEST, errorCode, "Email address is not valid.");
    }

}
