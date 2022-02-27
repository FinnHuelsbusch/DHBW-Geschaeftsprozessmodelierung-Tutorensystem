import { createContext } from 'react';
import { User, UserRole } from '../types/User';

type context = {
    loggedUser: User | undefined,
    login: (user: User) => void,
    logout: () => void,
    hasRoles: (roles: Array<UserRole>) => boolean
}

const hasRoles = (): boolean => {
    return false;
}

export const AuthContext = createContext<context>({
    loggedUser: undefined as User | undefined,
    login: (user: User) => { },
    logout: () => { },
    hasRoles: hasRoles
});