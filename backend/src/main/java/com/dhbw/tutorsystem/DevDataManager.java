package com.dhbw.tutorsystem;

import java.time.LocalDateTime;
import java.util.Set;

import com.dhbw.tutorsystem.role.ERole;
import com.dhbw.tutorsystem.role.Role;
import com.dhbw.tutorsystem.role.RoleRepository;
import com.dhbw.tutorsystem.user.User;
import com.dhbw.tutorsystem.user.UserRepository;
import com.dhbw.tutorsystem.user.director.Director;
import com.dhbw.tutorsystem.user.director.DirectorRepository;
import com.dhbw.tutorsystem.user.student.Student;
import com.dhbw.tutorsystem.user.student.StudentRepository;

import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DevDataManager {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final DirectorRepository directorRepository;
    private final StudentRepository studentRepository;

    public void initDatabaseForDevelopment() {
        Role rStudent = roleRepository.save(new Role(ERole.ROLE_STUDENT));
        Role rDirector = roleRepository.save(new Role(ERole.ROLE_DIRECTOR));
        Role rAdmin = roleRepository.save(new Role(ERole.ROLE_ADMIN));

        User uAdmin = new User("adam.admin@dhbw-mannheim.de", "1234");
        uAdmin.setRoles(Set.of(rAdmin));
        uAdmin.setPassword(encoder.encode(uAdmin.getPassword()));
        uAdmin.setEnabled(true);
        uAdmin.setLastPasswordAction(LocalDateTime.now());
        uAdmin = userRepository.save(uAdmin);

        Director uDirector = new Director("dirk.director@dhbw-mannheim.de", "1234");
        uDirector.setRoles(Set.of(rDirector));
        uDirector.setPassword(encoder.encode(uDirector.getPassword()));
        uDirector.setEnabled(true);
        uDirector.setLastPasswordAction(LocalDateTime.now());
        uDirector = directorRepository.save(uDirector);

        Student uStudent = new Student("s111111@student.dhbw-mannheim.de", "1234");
        uStudent.setRoles(Set.of(rStudent));
        uStudent.setPassword(encoder.encode(uStudent.getPassword()));
        uStudent.setEnabled(true);
        uStudent.setLastPasswordAction(LocalDateTime.now());
        uStudent = studentRepository.save(uStudent);
    }

}
