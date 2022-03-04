package com.dhbw.tutorsystem.security.authentication.exception;

import com.dhbw.tutorsystem.exception.TSBaseException;
import com.dhbw.tutorsystem.exception.TSErrorCode;

import org.springframework.http.HttpStatus;

public class MailSendException extends TSBaseException {

    private static final TSErrorCode errorCode = TSErrorCode.MAIL_SENDING_FAILED;

    public MailSendException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, errorCode, "Email could not be sent.");
    }

}
