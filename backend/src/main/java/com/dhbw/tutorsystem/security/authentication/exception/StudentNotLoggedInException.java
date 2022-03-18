package com.dhbw.tutorsystem.security.authentication.exception;

import com.dhbw.tutorsystem.exception.TSBaseException;
import com.dhbw.tutorsystem.exception.TSErrorCode;

import org.springframework.http.HttpStatus;

public class StudentNotLoggedInException extends TSBaseException {

    private static final TSErrorCode errorCode = TSErrorCode.STUDENT_NOT_LOGGED_IN;

    public StudentNotLoggedInException() {
        super(HttpStatus.UNAUTHORIZED, errorCode, "Student is not logged in.");
    }

}