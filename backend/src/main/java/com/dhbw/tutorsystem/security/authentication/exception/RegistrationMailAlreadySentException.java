package com.dhbw.tutorsystem.security.authentication.exception;

import com.dhbw.tutorsystem.exception.TSBaseException;
import com.dhbw.tutorsystem.exception.TSErrorCode;

import org.springframework.http.HttpStatus;

public class RegistrationMailAlreadySentException extends TSBaseException {

    private static final TSErrorCode errorCode = TSErrorCode.REGISTRATION_MAIL_ALREADY_SENT;

    public RegistrationMailAlreadySentException() {
        super(HttpStatus.BAD_REQUEST, errorCode, "Registration mail has already been sent out.");
    }

    public RegistrationMailAlreadySentException(String message) {
        super(HttpStatus.BAD_REQUEST, errorCode, message);
    }

}
