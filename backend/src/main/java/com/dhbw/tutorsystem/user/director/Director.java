package com.dhbw.tutorsystem.user.director;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import com.dhbw.tutorsystem.course.Course;
import com.dhbw.tutorsystem.user.User;

import lombok.Getter;
import lombok.Setter;

@Entity
public class Director extends User{

    @ManyToOne(fetch = FetchType.LAZY)
    @Getter
    @Setter
    private Course assignedCourses;

    public Director(){
        
    }

    public Director(String firstname, String lastname, String email, String password) {
        super(firstname, lastname, email, password); 
    }

    public Director(String email, String password) {
        super(email, password); 
    }
    
}
