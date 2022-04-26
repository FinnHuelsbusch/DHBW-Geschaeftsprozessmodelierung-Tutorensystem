package com.dhbw.tutorsystem.tutorial.exception;

import com.dhbw.tutorsystem.exception.TSBaseException;
import com.dhbw.tutorsystem.exception.TSErrorCode;

import org.springframework.http.HttpStatus;

public class InvalidTutorialParticipationStatusException extends TSBaseException {

    private static final TSErrorCode errorCode = TSErrorCode.INVALID_TUTORIAL_PARTICIPATION_STATUS;

    public InvalidTutorialParticipationStatusException() {
        super(HttpStatus.BAD_REQUEST, errorCode, "Invalid tutorial participation status.");
    }

    public InvalidTutorialParticipationStatusException(String message) {
        super(HttpStatus.BAD_REQUEST, errorCode, message);
    }

}
