import { createContext } from 'react';
import { User, UserRole } from '../types/User';

export type UserContext = {
    loggedUser: User | undefined,
    login: (user: User, remember?: boolean) => void,
    logout: () => void,
    hasRoles: (roles: Array<UserRole>) => boolean
}

// must initialize with dummy functions here
// in App.tsx, the actual functionality is defined
export const AuthContext = createContext<UserContext>({
    loggedUser: undefined as User | undefined,
    login: (user: User, remember: boolean = false) => { },
    logout: () => { },
    hasRoles: (roles: Array<UserRole>): boolean => false
});