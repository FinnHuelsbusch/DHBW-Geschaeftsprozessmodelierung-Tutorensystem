package com.dhbw.tutorsystem.course.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.dhbw.tutorsystem.course.Course;
import com.dhbw.tutorsystem.specialisationCourse.dto.SpecialisationCourseWithoutCourse;

import org.modelmapper.ModelMapper;

import lombok.Data;

@Data
public class CourseWithTitleAndSpecialisations {

    private Integer id;

    private String title;

    private String abbreviation;

    private Set<SpecialisationCourseWithoutCourse> specialisationCourses;

        // maps a given course to  a course with title, abbreviation and specialisationCourses as set
    public static CourseWithTitleAndSpecialisations convertToDto(ModelMapper modelMapper, Course course) {
        CourseWithTitleAndSpecialisations courseWithTitleAndSpecialisations = modelMapper.map(course,
                CourseWithTitleAndSpecialisations.class);
        return courseWithTitleAndSpecialisations;
    }

    // maps multiple given courses to  courses with title, abbreviation and specialisationCourses as set
    public static List<CourseWithTitleAndSpecialisations> convertToDto(ModelMapper modelMapper,
            Iterable<Course> courses) {
        ArrayList<CourseWithTitleAndSpecialisations> coursesList = new ArrayList<>();
        for (Course course : courses) {
            coursesList.add(convertToDto(modelMapper, course));
        }
        return coursesList;
    }
}
