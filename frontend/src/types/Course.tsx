import { User } from "./User"

export interface SpecialisationCourse {
    id: number,
    title: string,
    course: CourseWithTitleAndLeaders
}

export interface CourseWithTitleAndLeaders {
    id: number,
    title: string,
    leadBy: User[]
}

export interface CourseWithTitleAndSpecialisations {
    id: number,
    title: string,
    specialisationCourses: SpecialisationCoursesWithoutCourse[]
}

export interface SpecialisationCoursesWithoutCourse {
    id: number,
    title: string,
}