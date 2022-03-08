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
        createTutorial("Mathe I", "appointment", "Wir machen Analysis und lineare Algebra. Außerdem noch LOPs.", 120,
                LocalDate.now(), LocalDate.now().plusWeeks(2));
        createTutorial("Elektrotechnik 1", "appointment", "Wir machen Elektrotechnik jeden Montag um 17:00 Uhr.", 120,
                LocalDate.now(), LocalDate.now().plusWeeks(9));
        createTutorial("Elektrotechnik 2", "appointment", "Wir machen Elektrotechnik jeden Montag um 19:00 Uhr.", 120,
                LocalDate.now().plusWeeks(1), LocalDate.now().plusWeeks(10));
        createTutorial("Elektrotechnik 3", "appointment", "Wir machen Elektrotechnik jeden Montag um 9:00 Uhr.", 120,
                LocalDate.now().plusWeeks(2), LocalDate.now().plusWeeks(11));
        createTutorial("Elektrotechnik 4", "appointment", "Wir machen Elektrotechnik jeden Montag um 15:00 Uhr.", 120,
                LocalDate.now().plusWeeks(3), LocalDate.now().plusWeeks(12));
    }

    private Tutorial createTutorial(String title, String appointment, String description, int durationMinutes,
            LocalDate start, LocalDate end) {
        Tutorial t = new Tutorial();
        t.setTitle(title);
        t.setAppointment(appointment);
        t.setDescription(description);
        t.setDurationMinutes(durationMinutes);
        t.setStart(start);
        t.setEnd(end);
        return tutorialRepository.save(t);
    }

}
