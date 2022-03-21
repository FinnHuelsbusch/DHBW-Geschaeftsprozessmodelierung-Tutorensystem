package com.dhbw.tutorsystem.course.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.dhbw.tutorsystem.course.Course;
import com.dhbw.tutorsystem.user.dto.UserWithEmailAndName;

import org.modelmapper.ModelMapper;

import lombok.Data;

@Data
public class CourseWithTitleAndLeaders {

    private Integer id;

    private String title;

    private String abbreviation;

    private Set<UserWithEmailAndName> leadBy;

    // maps a given course to a course with title, abbreviation and leaders (user with email and name)
    public static CourseWithTitleAndLeaders convertToDto(ModelMapper modelMapper, Course course) {
        CourseWithTitleAndLeaders courseWithTitleAndLeaders = modelMapper.map(course, CourseWithTitleAndLeaders.class);
        return courseWithTitleAndLeaders;
    }

    // maps multiple given courses to  courses with title, abbreviation and leaders (user with email and name)
    public static List<CourseWithTitleAndLeaders> convertToDto(ModelMapper modelMapper, Iterable<Course> courses) {
        ArrayList<CourseWithTitleAndLeaders> coursesList = new ArrayList<>();
        for (Course course : courses) {
            coursesList.add(convertToDto(modelMapper, course));
        }
        return coursesList;
    }
}
