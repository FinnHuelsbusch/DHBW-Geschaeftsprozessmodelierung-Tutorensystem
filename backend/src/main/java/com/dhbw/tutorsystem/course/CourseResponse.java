package com.dhbw.tutorsystem.course;

import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.dhbw.tutorsystem.user.director.Director;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@AllArgsConstructor
public class CourseResponse {

    @AllArgsConstructor
    private class InnerUser{
        @Getter
        @Setter
        private String firstName;
    
        @Getter
        @Setter
        private String lastName;
    
        @Getter
        @Setter
        private String email; 
    }

    public CourseResponse(int id, String title, Set<Director> leadBy){
        this.title = title; 
        this.id = id; 
        this.leadBy = leadBy.stream().map((director) -> new InnerUser(director.getFirstName(), director.getLastName(), director.getEmail())).collect(Collectors.toSet()); 
    }

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
    private Set<InnerUser> leadBy;

    
}
