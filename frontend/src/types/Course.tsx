import { User } from "./User"

export interface SpecialisationCourse {
    id: number,
    title: string,
    course: Course
}

export interface Course {
    id: number,
    title: string,
    leadBy: [User]
}