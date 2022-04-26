import { User } from "./User"

export interface SpecialisationCourse {
    id: number,
    title: string,
    abbreviation: string,
    course: CourseWithTitleAndLeaders
}

export interface CourseWithTitleAndLeaders {
    id: number,
    title: string,
    abbreviation: string,
    leadBy: User[]
}

export interface CourseWithTitleAndSpecialisations {
    id: number,
    title: string,
    abbreviation: string,
    specialisationCourses: SpecialisationCoursesWithoutCourse[]
}

export interface SpecialisationCoursesWithoutCourse {
    id: number,
    title: string,
    abbreviation: string,
}