package com.dhbw.tutorsystem.user;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.dhbw.tutorsystem.course.Course;
import com.dhbw.tutorsystem.role.Role;
import com.dhbw.tutorsystem.tutorial.Tutorial;
import com.fasterxml.jackson.annotation.JsonIgnore;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
public class User {
    @JsonIgnore
    private static final String studentMailEnding = "@student.dhbw-mannheim.de";
    @JsonIgnore
    private static final String directorMailEnding = "@dhbw-mannheim.de";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Integer id;

    @Getter
    @Setter
    private String firstName;

    @Getter
    @Setter
    private String lastName;

    @JsonIgnore
    @Getter
    @Setter
    private String password;

    @Getter
    @Setter
    private String email;

    @ManyToMany(fetch = FetchType.LAZY)
    @Getter
    @Setter
    private Set<Role> roles = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @Getter
    @Setter
    private Course course;

    @ManyToMany
    @Getter
    @Setter
    private Set<Tutorial> participates;

    public User() {

    }

    public User(String firstname, String lastname, String email, String password) {
        this.firstName = firstname;
        this.lastName = lastname;
        this.email = email;
        this.password = password;
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public boolean isStudentMail() {
        return StringUtils.isNotBlank(this.email) && this.email.endsWith(studentMailEnding);
    }

    public boolean isDirectorMail() {
        return StringUtils.isNotBlank(this.email) && this.email.endsWith(directorMailEnding);
    }

}
