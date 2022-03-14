package com.dhbw.tutorsystem.tutorial.exception;

import com.dhbw.tutorsystem.exception.TSBaseException;
import com.dhbw.tutorsystem.exception.TSErrorCode;

import org.springframework.http.HttpStatus;

public class SpecialisationCourseNotFoundException extends TSBaseException {
    private static final TSErrorCode errorCode = TSErrorCode.SPECIALISATION_COURSE_NOT_FOUND;

    public SpecialisationCourseNotFoundException() {
        super(HttpStatus.NOT_FOUND, errorCode, "SpecialisationCourse does not exist.");
    }
}
