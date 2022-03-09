import { Sorting } from "./Paging";
import { Course } from "./Course";

export interface Tutorial {
    id: number,
    title: string,
    description: string,
    start: Date,
    end: Date,
    durationMinutes: number,
    tutors: any,
    specialisationCourses: Array<Course>
}

export interface TutorialFilter {
    text?: string,
    startDateFrom?: Date,
    startDateTo?: Date,
    specialisationCourseIds?: Array<number>,
    sorting?: Sorting,
    page: number,
    elementsPerPage: number
}

export interface TutorialFilterResponse {
    tutorials?: Array<Tutorial>,
    currentPage: number,
    totalPages: number,
    totalElements: number
}