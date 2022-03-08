import { Page } from "./Paging"

export interface Tutorial {
    title: string,
    description: string,
    start: Date,
    end: Date
}

export interface TutorialFilter extends Page {
    text?: string,
    startDateFrom?: Date,
    startDateTo?: Date,
    specialisationCourseIds?: Array<number>,
    tutorials?: Array<Tutorial>
}

export interface TutorialFilterResponse {
    tutorials: Array<Tutorial>,
    currentPage: number,
    totalPages: number,
    totalElements: number
}