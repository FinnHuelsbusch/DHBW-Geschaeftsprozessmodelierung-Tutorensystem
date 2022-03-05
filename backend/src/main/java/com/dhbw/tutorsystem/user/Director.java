package com.dhbw.tutorsystem.user;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import com.dhbw.tutorsystem.course.Course;

import lombok.Getter;
import lombok.Setter;

@Entity
public class Director extends User{

    @ManyToOne(fetch = FetchType.LAZY)
    @Getter
    @Setter
    private Course leadCourses;
    
}
