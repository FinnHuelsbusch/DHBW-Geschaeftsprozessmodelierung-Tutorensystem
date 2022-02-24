import axios from 'axios';

const backendUrl = 'http://localhost:8080';

const api = axios.create({
    baseURL: backendUrl,
});

export const ping = (): Promise<string> => {
    return api.get('/ping')
        .then(res => res.data);
}