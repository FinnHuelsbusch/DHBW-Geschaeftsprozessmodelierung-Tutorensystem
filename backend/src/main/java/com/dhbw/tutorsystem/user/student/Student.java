package com.dhbw.tutorsystem.user.student;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import com.dhbw.tutorsystem.specialisationCourse.SpecialisationCourse;
import com.dhbw.tutorsystem.tutorial.Tutorial;
import com.dhbw.tutorsystem.tutorialRequest.TutorialRequest;
import com.dhbw.tutorsystem.user.User;

import lombok.Getter;
import lombok.Setter;

@Entity
public class Student extends User {

    @ManyToMany(fetch = FetchType.LAZY)
    @Getter
    @Setter
    private Set<Tutorial> participates;

    @ManyToMany(fetch = FetchType.LAZY)
    @Getter
    @Setter
    private Set<Tutorial> markedTutorials;

    @ManyToMany(fetch = FetchType.LAZY)
    @Getter
    @Setter
    private Set<Tutorial> holds;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    private SpecialisationCourse specialisationCourse;

    @Getter
    @Setter
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<TutorialRequest> tutorialRequestsOfInterest;

    public Student() {

    }

    public Student(String firstname, String lastname, String email, String password) {
        super(firstname, lastname, email, password);
    }

    public Student(String email, String password) {
        super(email, password);
    }
}
