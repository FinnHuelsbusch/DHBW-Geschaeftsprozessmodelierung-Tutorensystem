package com.dhbw.tutorsystem.security.authentication.exception;

import com.dhbw.tutorsystem.exception.TSBaseException;
import com.dhbw.tutorsystem.exception.TSErrorCode;

import org.springframework.http.HttpStatus;

public class LastPasswordActionTooRecentException extends TSBaseException {

    private static final TSErrorCode errorCode = TSErrorCode.LAST_PASSWORD_ACTION_TOO_RECENT;

    public LastPasswordActionTooRecentException() {
        super(HttpStatus.BAD_REQUEST, errorCode, "Last password action is too recent. Please try again later.");
    }

}
