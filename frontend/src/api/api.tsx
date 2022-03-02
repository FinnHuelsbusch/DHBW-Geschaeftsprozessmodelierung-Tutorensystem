import { message } from 'antd';
import { ok } from 'assert';
import axios from 'axios';
import { RequestError } from '../types/RequestError';
import { User } from '../types/User';

const backendUrl = 'http://localhost:8080';

const api = axios.create({
    baseURL: backendUrl,
});

export const getRequestError = (err: any): RequestError => {
    return {
        statusCode: err.response.status,
        reason: err.response.data
    } as RequestError;
};

export const ping = (): Promise<string> => {
    return api.get('/ping')
        .then(res => res.data);
}

const applyJwt = (jwt: string) => {
    // do not persist jwt beyond the browser session
    api.defaults.headers.common['Authorization'] = `Bearer ${jwt}`;
}

export const login = (email: string, password: string): Promise<User> => {
    return api.post('/authentication/login',
        {
            email: email.trim(),
            password: password
        }).then(res => {
            const data = res.data;
            // handle missing token as error case
            if (!res.data.token) Promise.reject();
            const user = {
                email: data.email,
                roles: data.roles,
                jwt: data.token,
                loginExpirationDate: data.expirationDate
            } as User;
            applyJwt(user.jwt);
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

export const verifyAccount = (hash: string | null, email: string | null): Promise<string> => {
    if (!hash || !email) return Promise.reject();
    return api.post('/authentication/enableAccount', {
        hash: hash,
        email: email
    })
        .then(res => {
            return "ok";
        });
}

export const requestPasswordReset = (email: string): Promise<string> => {
    return api.post('/authentication/requestPasswordReset', {
        email: email.trim()
    }).then(res => "ok")
}

export const resetPassword = (hash: string | null, email: string | null, newPassword: string): Promise<string> => {
    if (!hash || !email) return Promise.reject();
    return api.post('/authentication/resetPassword', {
        hash: hash,
        email: email.trim(),
        password: newPassword
    }).then(res => "ok")
}