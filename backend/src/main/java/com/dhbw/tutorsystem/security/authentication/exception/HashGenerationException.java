package com.dhbw.tutorsystem.security.authentication.exception;

import com.dhbw.tutorsystem.exception.TSBaseException;
import com.dhbw.tutorsystem.exception.TSErrorCode;

import org.springframework.http.HttpStatus;

public class HashGenerationException extends TSBaseException {

    private static final TSErrorCode errorCode = TSErrorCode.HASH_GENERATION_EXCEPTION;

    public HashGenerationException() {
        super(HttpStatus.BAD_REQUEST, errorCode, "Hash value could not be generated.");
    }

    public HashGenerationException(String message) {
        super(HttpStatus.BAD_REQUEST, errorCode, message);
    }

}
