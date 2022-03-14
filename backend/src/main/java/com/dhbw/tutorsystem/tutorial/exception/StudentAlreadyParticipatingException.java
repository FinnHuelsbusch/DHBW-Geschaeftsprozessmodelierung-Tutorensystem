package com.dhbw.tutorsystem.tutorial.exception;

import com.dhbw.tutorsystem.exception.TSBaseException;
import com.dhbw.tutorsystem.exception.TSErrorCode;

import org.springframework.http.HttpStatus;

public class StudentAlreadyParticipatingException extends TSBaseException {

    private static final TSErrorCode errorCode = TSErrorCode.STUDENT_ALREADY_PARTICIPATING;

    public StudentAlreadyParticipatingException() {
        super(HttpStatus.BAD_REQUEST, errorCode, "Student is already participating.");
    }

}
