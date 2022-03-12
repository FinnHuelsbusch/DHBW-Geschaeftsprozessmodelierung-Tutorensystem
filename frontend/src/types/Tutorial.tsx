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
    numberOfParticipants: number
}

export interface Tutor {
    id: number,
    firstName: string,
    lastName: string,
    email: string
}