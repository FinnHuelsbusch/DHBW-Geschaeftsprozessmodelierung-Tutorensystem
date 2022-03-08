package com.dhbw.tutorsystem.tutorial;

import java.time.LocalDate;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.dhbw.tutorsystem.specialisationCourse.SpecialisationCourse;
import com.dhbw.tutorsystem.user.User;
import com.dhbw.tutorsystem.user.student.Student;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tutorial")
public class Tutorial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Integer id;

    @Getter
    @Setter
    private String appointment;

    @Getter
    @Setter
    private String description;

    @Getter
    @Setter
    private int durationMinutes;

    @Getter
    @Setter
    private LocalDate start;

    @Getter
    @Setter
    private LocalDate end;

    @ManyToMany(fetch = FetchType.LAZY)
    @Getter
    @Setter
    private Set<User> tutors;

    @ManyToMany(fetch = FetchType.LAZY)
    @Getter
    @Setter
    private Set<Student> participants;

    @ManyToMany(fetch = FetchType.LAZY)
    @Getter
    @Setter
    private Set<SpecialisationCourse> specialisationCourse;

    @Getter
    @Setter
    private String title;
}
