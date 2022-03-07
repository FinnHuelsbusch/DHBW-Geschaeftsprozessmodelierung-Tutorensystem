package com.dhbw.tutorsystem.specialisationCourse;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.dhbw.tutorsystem.course.Course;

import lombok.Getter;
import lombok.Setter;

@Entity
public class SpecialisationCourse {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Integer id;

    @Getter
    @Setter
    private String title;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    private Course course; 
    

}
