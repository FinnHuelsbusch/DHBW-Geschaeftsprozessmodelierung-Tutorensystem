import { Sorting } from "./Paging"

export interface Tutorial {
    title: string,
    description: string,
    start: Date,
    end: Date
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