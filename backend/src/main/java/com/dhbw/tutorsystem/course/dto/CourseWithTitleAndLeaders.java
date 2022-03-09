package com.dhbw.tutorsystem.course.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.dhbw.tutorsystem.course.Course;
import com.dhbw.tutorsystem.user.director.Director;
import com.dhbw.tutorsystem.user.dto.UserWithEmailAndName;

import org.modelmapper.ModelMapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Data
public class CourseWithTitleAndLeaders {

    private Integer id;

    private String title;

    private Set<UserWithEmailAndName> leadBy;

    public static CourseWithTitleAndLeaders convertToDto(ModelMapper modelMapper, Course course){
        CourseWithTitleAndLeaders courseWithTitleAndLeaders = modelMapper.map(course, CourseWithTitleAndLeaders.class); 
        return courseWithTitleAndLeaders;
    }

    public static List<CourseWithTitleAndLeaders> convertToDto(ModelMapper modelMapper, Iterable<Course> courses){
        ArrayList<CourseWithTitleAndLeaders> coursesList = new ArrayList<>(); 
        for (Course course : courses) {
            coursesList.add(convertToDto(modelMapper, course)); 
        }
        return coursesList;
    }
}
