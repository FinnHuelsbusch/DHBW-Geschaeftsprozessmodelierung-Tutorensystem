import axios from 'axios';
import moment from 'moment';
import { CourseWithTitleAndLeaders, CourseWithTitleAndSpecialisations } from '../types/Course';
import { ErrorCode, RequestError } from '../types/RequestError';
import { mapTutorialFromResponse, Tutorial, TutorialFilter, TutorialFilterResponse, TutorialRequest } from '../types/Tutorial';
import { RegisterRequest, User, UserWithMailAndNameAndId } from '../types/User';

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

export const register = (registerRequest: RegisterRequest): Promise<string> => {
    return api.post('/authentication/register',
        {
            ...registerRequest
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

export const createTutorialRequest = (tutorialRequest: TutorialRequest): Promise<void> => {
    return api.put('/tutorialrequest',
        {
            description: tutorialRequest.description,
            title: tutorialRequest.title,
            semester: tutorialRequest.semester,
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

export const getFilteredTutorials = (filter: TutorialFilter): Promise<TutorialFilterResponse> => {
    const sorting = filter.sorting.attribute !== "none"
        ? `&sort=${filter.sorting.attribute},${filter.sorting.order}`
        : "";
    const attributesFilter = {
        text: filter.text?.trim(),
        startDateFrom: filter.startDateFrom,
        startDateTo: filter.startDateTo,
        specialisationCourseIds: filter.specialisationCourseIds,
        selectMarked: filter.selectMarked,
        selectParticipates: filter.selectParticipates,
        selectHolds: filter.selectHolds
    } as TutorialFilter;
    return api.post(`/tutorials/findWithFilter?page=${filter.page}&size=${filter.elementsPerPage}${sorting}`, attributesFilter)
        .then(res => {
            const data = res.data;
            return {
                tutorials: data.tutorials.map((t: any) => mapTutorialFromResponse(t)) as Array<Tutorial>,
                currentPage: data.currentPage,
                totalPages: data.totalPages,
                totalElements: data.totalElements
            } as TutorialFilterResponse;
        });
}

export const getTutorial = (tutorialId: number): Promise<Tutorial> => {
    return api.get(`/tutorials/${tutorialId}`)
        .then(res => {
            const data = res.data;
            return mapTutorialFromResponse(data);
        });
}

export const participateInTutorial = (tutorialId: number): Promise<any> => {
    return api.put(`/tutorials/participate/${tutorialId}`)
        .then(res => {
            return res.data;
        });
}

export const removeParticipationInTutorial = (tutorialId: number): Promise<any> => {
    return api.delete(`/tutorials/participate/${tutorialId}`)
        .then(res => {
            return res.data;
        });
}

export const markTutorial = (tutorialId: number): Promise<any> => {
    return api.put(`/tutorials/mark/${tutorialId}`)
        .then(res => {
            return res.data;
        });
}

export const unmarkTutorial = (tutorialId: number): Promise<any> => {
    return api.delete(`/tutorials/mark/${tutorialId}`)
        .then(res => {
            return res.data;
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

export const getUsersWithNameAndMailAndId = (): Promise<UserWithMailAndNameAndId[]> => {
    return api.get('/users')
        .then(res => res.data);
}

export const putTutorial = (newTutorial: Object): Promise<number> => {
    return api.put('/tutorials', newTutorial)
        .then(res => {
            const data = res.data;
            return data.id;
        });
}

export const deleteTutorial = (tutorialId: number, reason?: string): Promise<number> => {
    return api.post(`/tutorials/delete/${tutorialId}`, { reason: reason })
        .then(res => {
            const data = res.data;
            return data.id;
        });
}