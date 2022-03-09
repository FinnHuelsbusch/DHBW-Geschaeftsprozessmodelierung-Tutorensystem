import { User } from "./User"

export interface SpecialisationCourse {
    id: number,
    title: string,
    course: CourseWithEmailAndName
}

export interface CourseWithEmailAndName {
    id: number,
    title: string,
    leadBy: User[]
}