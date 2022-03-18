export interface RequestError {
    status: number,
    message: string,
    timestamp: Date,
    errorCode: ErrorCode
}

export enum ErrorCode {
    ACCOUNT_NOT_ENABLED,
    BAD_REQUEST,
    EMAIL_ALREADY_EXISTS,
    INTERNAL_SERVER_ERROR,
    INVALID_EMAIL,
    LAST_PASSWORD_ACTION_TOO_RECENT,
    LOGIN_FAILED,
    ROLE_NOT_FOUND,
    USER_ALREADY_ENABLED,
    USER_NOT_FOUND,
}


export const getErrorMessageString = (errorCode: ErrorCode) => {
    switch (errorCode) {
        case ErrorCode.ACCOUNT_NOT_ENABLED:
            return "Konto wurde noch nicht aktiviert";
        case ErrorCode.BAD_REQUEST:
            return "Interner Fehler, bitte kontaktieren Sie den Administrator";
        case ErrorCode.EMAIL_ALREADY_EXISTS:
            return "E-Mail Adresse existiert bereits";
        case ErrorCode.INTERNAL_SERVER_ERROR:
            return "Interner Fehler, bitte kontaktieren Sie den Administrator";
        case ErrorCode.INVALID_EMAIL:
            return "E-Mail Adresse ungültig";
        case ErrorCode.LAST_PASSWORD_ACTION_TOO_RECENT:
            return "Letzte Passwort-Aktion zu aktuell, bitte versuchen Sie es später erneut";
        case ErrorCode.LOGIN_FAILED:
            return "Login fehlgeschlagen";
        case ErrorCode.ROLE_NOT_FOUND:
            return "Nutzerrolle nicht gefunden";
        case ErrorCode.USER_ALREADY_ENABLED:
            return "Nutzer ist bereits aktiviert";
        case ErrorCode.USER_NOT_FOUND:
            return "Nutzer wurde nicht gefunden";
        default:
            return "Unbekannter Fehler";
    }
}