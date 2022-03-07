import { createContext } from 'react';
import { User, UserRole } from '../types/User';

export type UserContext = {
    loggedUser: User | undefined,
    login: (user: User, remember?: boolean) => void,
    logout: () => void,
    hasRoles: (roles: Array<UserRole>) => boolean
}

const hasRoles = (): boolean => {
    return false;
}

export const AuthContext = createContext<UserContext>({
    loggedUser: undefined as User | undefined,
    login: (user: User, remember: boolean = false) => { },
    logout: () => { },
    hasRoles: hasRoles
});