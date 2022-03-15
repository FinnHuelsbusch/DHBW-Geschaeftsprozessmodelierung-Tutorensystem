package com.dhbw.tutorsystem.tutorial.exception;

import com.dhbw.tutorsystem.exception.TSBaseException;
import com.dhbw.tutorsystem.exception.TSErrorCode;

import org.springframework.http.HttpStatus;

public class InvalidTutorialMarkException extends TSBaseException {

    private static final TSErrorCode errorCode = TSErrorCode.INVALID_TUTORIAL_MARK;

    public InvalidTutorialMarkException() {
        super(HttpStatus.BAD_REQUEST, errorCode, "Invalid tutorial mark.");
    }

    public InvalidTutorialMarkException(String message) {
        super(HttpStatus.BAD_REQUEST, errorCode, message);
    }
}
