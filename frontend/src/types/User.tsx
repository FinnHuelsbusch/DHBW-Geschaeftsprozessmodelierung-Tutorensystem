export enum UserRole {
    ROLE_USER = "ROLE_USER",
    ROLE_ADMIN = "ROLE_ADMIN",
    ROLE_DIRECTOR = "ROLE_DIRECTOR"
}

export interface User {
    username: string,
    password?: string,
    roles: Array<UserRole>,
    token: string,
    id: number
}

export interface JWTUser extends User {
    type: string,
    expirationDate: Date
}