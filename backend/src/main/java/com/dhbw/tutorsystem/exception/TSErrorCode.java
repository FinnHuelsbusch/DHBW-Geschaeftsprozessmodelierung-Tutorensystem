package com.dhbw.tutorsystem.exception;

/*
    Please keep the enum values in sync with possible exceptions.
    When inserting a new TSErrorCode, please insert it in alphabetical order.
    To sort the list manually in VSCode, use CTRL+P, type '>', type 'Sort Lines Ascending'.
*/
public enum TSErrorCode {
    ACCOUNT_ENABLE_FAILED,
    EMAIL_ALREADY_EXISTS,
    HASH_GENERATION_EXCEPTION,
    INTERNAL_SERVER_ERROR,
    INVALID_EMAIL,
    INVALID_HASH,
    LAST_PASSWORD_ACTION_TOO_RECENT,
    MAIL_SENDING_FAILED,
    REGISTRATION_MAIL_ALREADY_SENT,
    RESET_PASSWORD_ACCOUNT_NOT_ENABLED,
    ROLE_NOT_FOUND,
    USER_NOT_FOUND,
    USER_ALREADY_ENABLED
}
