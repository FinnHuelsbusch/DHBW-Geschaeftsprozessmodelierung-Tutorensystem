package com.dhbw.tutorsystem.user;

import com.dhbw.tutorsystem.role.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
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

    private final String studentMailEnding = "@student.dhbw-mannheim.de";
    private final String directorMailEnding = "@dhbw-mannheim.de";

    public User() {

    }

    public User(String firstname, String lastname, String email, String password) {
        this.firstName = firstname;
        this.lastName = lastname;
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
