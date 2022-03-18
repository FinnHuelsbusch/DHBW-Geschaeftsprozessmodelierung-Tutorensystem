import { Sorting } from "./Paging";
import { SpecialisationCourse } from "./Course"

export interface Tutorial {
    id: number,
    title: string,
    description: string,
    start: Date,
    end: Date,
    durationMinutes: number,
    tutors: Array<Tutor>,
    specialisationCourses: Array<SpecialisationCourse>,
    numberOfParticipants: number,
    isMarked?: boolean,
    participates?: boolean,
    holds?: boolean
}

export const mapTutorialFromResponse = (data: any): Tutorial => {
    return {
        id: data.id,
        title: data.title,
        description: data.description,
        start: data.start,
        end: data.end,
        durationMinutes: data.durationMinutes,
        tutors: data.tutors,
        specialisationCourses: data.specialisationCourses,
        numberOfParticipants: data.numberOfParticipants,
        isMarked: data.marked,
        participates: data.participates,
        holds: data.holds
    } as Tutorial;
}

export interface Tutor {
    id: number,
    firstName: string,
    lastName: string,
    email: string
}

export interface TutorialFilter {
    text?: string,
    startDateFrom?: string,
    startDateTo?: string,
    specialisationCourseIds?: Array<number>,
    selectMarked?: boolean,
    selectParticipates?: boolean,
    selectHolds?: boolean,
    sorting: Sorting,
    page: number,
    elementsPerPage: number,
}

export interface TutorialFilterResponse {
    tutorials?: Array<Tutorial>,
    currentPage: number,
    totalPages: number,
    totalElements: number
}