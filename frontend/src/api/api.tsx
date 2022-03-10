import axios from 'axios';
import moment from 'moment';
import { CourseWithTitleAndLeaders, CourseWithTitleAndSpecialisations } from '../types/Course';
import { ErrorCode, RequestError } from '../types/RequestError';
import { User } from '../types/User';

const backendUrl = process.env.REACT_APP_BACKEND_URL;

const api = axios.create({
    baseURL: backendUrl,
});


export const applyUserLogin = (user: User, saveToLocalStorage: boolean = false) => {
    // persist login if method parameter says so, or if it was persisted previously (e.g. refresh case)
    if (saveToLocalStorage || retrieveUserLocalStorage()) {
        localStorage.setItem("user", JSON.stringify(user));
    }
    api.defaults.headers.common['Authorization'] = `Bearer ${user.jwt}`;
}

export const retrieveUserLocalStorage = (): User | undefined => {
    const userString = localStorage.getItem("user");
    if (userString) {
        return JSON.parse(userString) as User;
    }
}

export const isUserLoginExpired = (loginExpirationDate: Date) => {
    return moment(loginExpirationDate).isBefore(moment.now());
}

export const removeUserLogin = () => {
    localStorage.removeItem("user");
    api.defaults.headers.common['Authorization'] = "";
}


export const getRequestError = (err: any): RequestError => {
    return {
        status: err.response.data.status,
        message: err.response.data.message,
        timestamp: err.response.data.timestamp,
        // map error code string to ErrorCode enum
        errorCode: ErrorCode[err.response.data.errorCode as keyof typeof ErrorCode]
    } as RequestError;
};

export const ping = (): Promise<string> => {
    console.log(backendUrl)
    return api.get('/ping')
        .then(res => res.data);
}

export const login = (email: string, password: string, remember: boolean = false): Promise<User> => {
    return api.post('/authentication/login',
        {
            email: email.trim(),
            password: password
        }).then(res => {
            const data = res.data;
            if (!res.data.token) Promise.reject();
            const user = {
                email: data.email,
                roles: data.roles,
                jwt: data.token,
                loginExpirationDate: data.expirationDate
            } as User;
            return user;
        });
}

export const register = (email: string, password: string): Promise<string> => {
    return api.post('/authentication/register',
        {
            email: email.trim(),
            password: password
        }).then(res => {
            const data = res.data;
            return data;
        });
}

export const enableAccount = (hash: string | null, email: string | null): Promise<User> => {
    if (!hash || !email) return Promise.reject();
    return api.post('/authentication/enableAccount', {
        // + signs will be removed from urlParams but must be part of the hash
        // so re-add them here
        hash: hash.replaceAll(" ", "+"),
        email: email
    })
        .then(res => {
            const data = res.data;
            if (!res.data.token) Promise.reject();
            const user = {
                email: data.email,
                roles: data.roles,
                jwt: data.token,
                loginExpirationDate: data.expirationDate
            } as User;
            return user;
        });
}

export const requestPasswordReset = (email: string, newPassword: string): Promise<string> => {
    return api.post('/authentication/requestPasswordReset', {
        email: email.trim(),
        newPassword: newPassword.trim()
    }).then(res => "ok")
}

export const performPasswordReset = (hash: string | null, email: string | null, newPassword: string): Promise<User> => {
    if (!hash || !email || !newPassword) return Promise.reject();
    return api.post('/authentication/performPasswordReset', {
        // + signs will be removed from urlParams but must be part of the hash
        // so re-add them here
        hash: hash.replaceAll(" ", "+"),
        email: email,
        newPassword: newPassword.trim()
    })
        .then(res => {
            const data = res.data;
            if (!res.data.token) Promise.reject();
            const user = {
                email: data.email,
                roles: data.roles,
                jwt: data.token,
                loginExpirationDate: data.expirationDate
            } as User;
            return user;
        });
}

export const changePassword = (newPassword: string): Promise<User> => {
    if (!newPassword) return Promise.reject();
    return api.post('/authentication/changePassword', {
        newPassword: newPassword.trim()
    }).then(res => {
        const data = res.data;
        if (!res.data.token) Promise.reject();
        const user = {
            email: data.email,
            roles: data.roles,
            jwt: data.token,
            loginExpirationDate: data.expirationDate
        } as User;
        return user;
    });
}

export const getCoursesWithTitleAndLeaders = (): Promise<CourseWithTitleAndLeaders[]> => {
    return api.get('/courses/withTitleAndLeaders')
        .then(res => res.data);
}

export const getCoursesWithTitleAndSpecialisations = (): Promise<CourseWithTitleAndSpecialisations[]> => {
    return api.get('/courses/withTitleAndSpecialisations')
        .then(res => res.data);
}