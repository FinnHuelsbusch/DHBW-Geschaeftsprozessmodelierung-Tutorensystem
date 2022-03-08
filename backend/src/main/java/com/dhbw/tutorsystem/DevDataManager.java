package com.dhbw.tutorsystem;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import com.dhbw.tutorsystem.role.ERole;
import com.dhbw.tutorsystem.role.Role;
import com.dhbw.tutorsystem.role.RoleRepository;
import com.dhbw.tutorsystem.tutorial.Tutorial;
import com.dhbw.tutorsystem.tutorial.TutorialRepository;
import com.dhbw.tutorsystem.user.User;
import com.dhbw.tutorsystem.user.UserRepository;
import com.dhbw.tutorsystem.user.director.Director;
import com.dhbw.tutorsystem.user.director.DirectorRepository;
import com.dhbw.tutorsystem.user.student.Student;
import com.dhbw.tutorsystem.user.student.StudentRepository;

import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DevDataManager {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final DirectorRepository directorRepository;
    private final StudentRepository studentRepository;
    private final TutorialRepository tutorialRepository;

    private User uAdmin;
    private Director uDirector;
    private Student uStudent;

    public void initDatabaseForDevelopment() {
        Role rStudent = roleRepository.save(new Role(ERole.ROLE_STUDENT));
        Role rDirector = roleRepository.save(new Role(ERole.ROLE_DIRECTOR));
        Role rAdmin = roleRepository.save(new Role(ERole.ROLE_ADMIN));

        uAdmin = new User("adam.admin@dhbw-mannheim.de", "1234");
        uAdmin.setRoles(Set.of(rAdmin));
        uAdmin.setPassword(encoder.encode(uAdmin.getPassword()));
        uAdmin.setEnabled(true);
        uAdmin.setLastPasswordAction(LocalDateTime.now());
        uAdmin = userRepository.save(uAdmin);

        uDirector = new Director("dirk.director@dhbw-mannheim.de", "1234");
        uDirector.setRoles(Set.of(rDirector));
        uDirector.setPassword(encoder.encode(uDirector.getPassword()));
        uDirector.setEnabled(true);
        uDirector.setLastPasswordAction(LocalDateTime.now());
        uDirector = directorRepository.save(uDirector);

        uStudent = new Student("s111111@student.dhbw-mannheim.de", "1234");
        uStudent.setRoles(Set.of(rStudent));
        uStudent.setPassword(encoder.encode(uStudent.getPassword()));
        uStudent.setEnabled(true);
        uStudent.setLastPasswordAction(LocalDateTime.now());
        uStudent = studentRepository.save(uStudent);

        insertTutorials();
    }

    public void insertTutorials() {
        Tutorial t1 = new Tutorial();
        t1.setTitle("Mathe I");
        t1.setAppointment("appointment");
        t1.setDescription("Wir machen Analysis und lineare Algebra. Außerdem noch LOPs.");
        t1.setDurationMinutes(120);
        t1.setStart(LocalDate.now());
        t1.setEnd(LocalDate.now().plusWeeks(2));
        t1 = tutorialRepository.save(t1);

        Tutorial t2 = new Tutorial();
        t2.setTitle("Elektrotechnik 2");
        t2.setAppointment("appointment");
        t2.setDescription("Wir machen Elektrotechnik jeden Montag um 19:00 Uhr.");
        t2.setDurationMinutes(120);
        t2.setStart(LocalDate.now().minusWeeks(2));
        t2.setEnd(LocalDate.now().plusWeeks(4));
        t2 = tutorialRepository.save(t1);
    }

}
