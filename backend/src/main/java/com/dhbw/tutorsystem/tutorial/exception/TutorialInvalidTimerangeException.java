package com.dhbw.tutorsystem.tutorial.exception;

import com.dhbw.tutorsystem.exception.TSBaseException;
import com.dhbw.tutorsystem.exception.TSErrorCode;

import org.springframework.http.HttpStatus;

public class TutorialInvalidTimerangeException extends TSBaseException {
    private static final TSErrorCode errorCode = TSErrorCode.TUTORIAL_INVALID_TIMERANGE;

    public TutorialInvalidTimerangeException() {
        super(HttpStatus.BAD_REQUEST, errorCode, "The start and end are not valid. Earliest start is today.");
    }
}
