
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

export interface UserWithMailAndNameAndId {
    id: number,
    email: string,
    firstName: string,
    lastName: string
}

export interface RegisterRequest {
    email: string,
    password: string,
    firstName: string,
    lastName: string,
    specialisationCourseId?: number
}

export interface UpdateUserInfo {
    email?: string,
    firstName: string,
    lastName: string,
    specialisationCourseId?: number
}