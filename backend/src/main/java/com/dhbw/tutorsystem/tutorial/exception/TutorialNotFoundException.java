package com.dhbw.tutorsystem.tutorial.exception;

import com.dhbw.tutorsystem.exception.TSBaseException;
import com.dhbw.tutorsystem.exception.TSErrorCode;

import org.springframework.http.HttpStatus;

public class TutorialNotFoundException extends TSBaseException {
    private static final TSErrorCode errorCode = TSErrorCode.TUTORIAL_NOT_FOUND;

    public TutorialNotFoundException() {
        super(HttpStatus.NOT_FOUND, errorCode, "Requested tutorial was not found.");
    }
}
