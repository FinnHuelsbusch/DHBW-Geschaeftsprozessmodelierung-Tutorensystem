package com.dhbw.tutorsystem.exception;

/*
    Please keep the enum values in sync with exception classes AND with Frontend ErrorCodes and handling!
    When inserting a new TSErrorCode, please insert it in alphabetical order.
    To sort the list manually in VSCode, use CTRL+P, type '>', type 'Sort Lines Ascending'.
*/
public enum TSErrorCode {
    ACCOUNT_NOT_ENABLED,
    BAD_REQUEST,
    EMAIL_ALREADY_EXISTS,
    INTERNAL_SERVER_ERROR,
    INVALID_EMAIL,
    LAST_PASSWORD_ACTION_TOO_RECENT,
    LOGIN_FAILED,
    ROLE_NOT_FOUND,
    STUDENT_NOT_LOGGED_IN,
    USER_ALREADY_ENABLED,
    USER_NOT_FOUND,
}
