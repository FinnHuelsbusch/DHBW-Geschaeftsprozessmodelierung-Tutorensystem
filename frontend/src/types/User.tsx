
export enum UserRole {
    ROLE_USER = "ROLE_USER",
    ROLE_ADMIN = "ROLE_ADMIN",
    ROLE_DIRECTOR = "ROLE_DIRECTOR"
}
export interface User {
    email: string,
    jwt: string,
    refreshToken: string,
    roles: Array<UserRole>
}