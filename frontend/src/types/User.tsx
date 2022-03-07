
export enum UserRole {
    ROLE_STUDENT = "ROLE_STUDENT",
    ROLE_ADMIN = "ROLE_ADMIN",
    ROLE_DIRECTOR = "ROLE_DIRECTOR"
}
export interface User {
    email: string,
    jwt: string,
    refreshToken: string,
    roles: Array<UserRole>,
    loginExpirationDate: Date
}