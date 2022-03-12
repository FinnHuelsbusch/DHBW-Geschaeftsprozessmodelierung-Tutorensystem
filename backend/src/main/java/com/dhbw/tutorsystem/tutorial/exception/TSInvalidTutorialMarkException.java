package com.dhbw.tutorsystem.tutorial.exception;

import com.dhbw.tutorsystem.exception.TSBaseException;
import com.dhbw.tutorsystem.exception.TSErrorCode;

import org.springframework.http.HttpStatus;

public class TSInvalidTutorialMarkException extends TSBaseException {

    private static final TSErrorCode errorCode = TSErrorCode.INVALID_TUTORIAL_MARK;

    public TSInvalidTutorialMarkException() {
        super(HttpStatus.BAD_REQUEST, errorCode, "Invalid tutorial mark.");
    }

    public TSInvalidTutorialMarkException(String message) {
        super(HttpStatus.BAD_REQUEST, errorCode, message);
    }
}
