package com.dhbw.tutorsystem.course;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "course")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Integer id;

    @Getter
    @Setter
    private String description;

    @Getter
    @Setter
    private int year;

    @Getter
    @Setter
    private String specialization;

    @Getter
    @Setter
    private String courseOfDegree;

    @Getter
    @Setter
    private char block;
}
