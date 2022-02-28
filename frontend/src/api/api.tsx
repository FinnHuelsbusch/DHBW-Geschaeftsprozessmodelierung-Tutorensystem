import axios from 'axios';
import { User } from '../types/User';

const backendUrl = 'http://localhost:8080';

const api = axios.create({
    baseURL: backendUrl,
});

export const ping = (): Promise<string> => {
    return api.get('/ping')
        .then(res => res.data);
}

const applyJwt = (jwt: string) => {
    // TODO: decide between persistent or non-persistent login
    // localStorage.setItem("jwt", jwt);
    api.defaults.headers.common['Authorization'] = `Bearer ${jwt}`;
}

export const login = (email: string, password: string): Promise<User> => {
    return api.post('/authentication/signin',
        {
            "email": email,
            "password": password
        }).then(res => {
            const data = res.data;
            // handle missing token as error case
            if (!res.data.accessToken) Promise.reject();
            const user = {
                email: data.email,
                roles: data.roles,
                jwt: data.accessToken
            } as User;
            applyJwt(user.jwt);
            return user;
        });
}