package com.dhbw.tutorsystem;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.dhbw.tutorsystem.course.Course;
import com.dhbw.tutorsystem.course.CourseRepository;
import com.dhbw.tutorsystem.role.ERole;
import com.dhbw.tutorsystem.role.Role;
import com.dhbw.tutorsystem.role.RoleRepository;
import com.dhbw.tutorsystem.specialisationCourse.SpecialisationCourse;
import com.dhbw.tutorsystem.specialisationCourse.SpecialisationCourseRepository;
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
    private final CourseRepository courseRepository;
    private final SpecialisationCourseRepository specialisationCourseRepository;

    private User uAdmin;
    private Director uDirector1, uDirector2, uDirector3;
    private Student uStudent1, uStudent2;
    private Student uTutor;

    private Course course1, course2;
    private SpecialisationCourse specialisationCourseSE, specialisationCourseSC, specialisationCourseAMB;

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

        Director uDirector1 = new Director("dirk.director@dhbw-mannheim.de", "1234");
        uDirector1.setRoles(Set.of(rDirector));
        uDirector1.setPassword(encoder.encode(uDirector1.getPassword()));
        uDirector1.setEnabled(true);
        uDirector1.setLastPasswordAction(LocalDateTime.now());
        uDirector1 = directorRepository.save(uDirector1);

        Director uDirector2 = new Director("daniel.director@dhbw-mannheim.de", "1234");
        uDirector2.setRoles(Set.of(rDirector));
        uDirector2.setPassword(encoder.encode(uDirector2.getPassword()));
        uDirector2.setEnabled(true);
        uDirector2.setLastPasswordAction(LocalDateTime.now());
        uDirector2 = directorRepository.save(uDirector2);

        Director uDirector3 = new Director("doris.director@dhbw-mannheim.de", "1234");
        uDirector3.setRoles(Set.of(rDirector));
        uDirector3.setPassword(encoder.encode(uDirector3.getPassword()));
        uDirector3.setEnabled(true);
        uDirector3.setLastPasswordAction(LocalDateTime.now());
        uDirector3 = directorRepository.save(uDirector3);

        Student uStudent1 = new Student("s111111@student.dhbw-mannheim.de", "1234");
        uStudent1.setRoles(Set.of(rStudent));
        uStudent1.setPassword(encoder.encode(uStudent1.getPassword()));
        uStudent1.setEnabled(true);
        uStudent1 = studentRepository.save(uStudent1);

        Student uStudent2 = new Student("s222222@student.dhbw-mannheim.de", "1234");
        uStudent2.setRoles(Set.of(rStudent));
        uStudent2.setPassword(encoder.encode(uStudent2.getPassword()));
        uStudent2.setEnabled(true);
        uStudent2 = studentRepository.save(uStudent2);

        uTutor = new Student("s999999@student.dhbw-mannheim.de", "1234");
        uTutor.setFirstName("Tarik");
        uTutor.setLastName("Tutor");
        uTutor.setRoles(Set.of(rStudent));
        uTutor.setPassword(encoder.encode(uTutor.getPassword()));
        uTutor.setEnabled(true);
        uTutor.setLastPasswordAction(LocalDateTime.now());
        uTutor = studentRepository.save(uTutor);

        insertTutorials();
        insertCourses();
        insertSpecialisationCourses();
    }

    public void insertTutorials() {
        createTutorial("Mathe I", "appointment", "Wir machen Analysis und lineare Algebra. Außerdem noch LOPs.",
                120,
                LocalDate.now(), LocalDate.now().plusWeeks(2), Set.of(uTutor), Set.of(uStudent1), null);
        createTutorial("Elektrotechnik 1", "appointment",
                "Wir machen Elektrotechnik jeden Montag um 17:00 Uhr.", 120,
                LocalDate.now(), LocalDate.now().plusWeeks(9), Set.of(uTutor), Set.of(uStudent1), null);
        createTutorial("Elektrotechnik 2", "appointment",
                "Wir machen Elektrotechnik jeden Montag um 19:00 Uhr.", 120,
                LocalDate.now().plusWeeks(1), LocalDate.now().plusWeeks(10), Set.of(uTutor),
                Set.of(uStudent1), null);
        createTutorial("Elektrotechnik 3", "appointment", "Wir machen Elektrotechnik jeden Montag um 9:00 Uhr.",
                120,
                LocalDate.now().plusWeeks(2), LocalDate.now().plusWeeks(11), Set.of(uTutor),
                Set.of(uStudent1), null);
        createTutorial("Elektrotechnik 4", "appointment",
                "Wir machen Elektrotechnik jeden Montag um 15:00 Uhr.", 120,
                LocalDate.now().plusWeeks(3), LocalDate.now().plusWeeks(12), Set.of(uTutor),
                Set.of(uStudent1), null);

        Tutorial tutorial1 = createTutorial(
                "Mathe 1",
                "Jeden Dienstag von 18 bis 19 Uhr.",
                "Mathe für alle, die den ersten Versuch nicht bestanden haben. Schwerpunkt Analysis.",
                120,
                LocalDate.now().plusWeeks(3),
                LocalDate.now().plusDays(21),
                Set.of(uTutor),
                Set.of(uStudent1),
                Set.of(specialisationCourseSE, specialisationCourseSC));

        Tutorial tutorial2 = createTutorial(
                "Programmieren 1",
                "Jeden Dienstag von 18 bis 19 Uhr.",
                "Aufgaben im Bereich programmieren mit Java. Schwerpunkt Objektorientierung",
                120,
                LocalDate.now().plusWeeks(3),
                LocalDate.now().plusWeeks(12),
                Set.of(uTutor),
                Set.of(uStudent1),
                Set.of(specialisationCourseSE, specialisationCourseSC));
    }

    private Tutorial createTutorial(String title, String appointment, String description, int durationMinutes,
            LocalDate start, LocalDate end, Set<User> tutors, Set<Student> participants,
            Set<SpecialisationCourse> specialisationCourses) {
        Tutorial t = new Tutorial();
        t.setTitle(title);
        t.setAppointment(appointment);
        t.setDescription(description);
        t.setDurationMinutes(durationMinutes);
        t.setStart(start);
        t.setEnd(end);
        t.setTutors(tutors);
        t.setParticipants(participants);
        t.setSpecialisationCourses(specialisationCourses);
        t = tutorialRepository.save(t);
        for (Student s : participants) {
            Set<Tutorial> participates = s.getParticipates();
            if (participates == null) {
                participates = new HashSet<Tutorial>();
            }
            participates.add(t);
            s.setParticipates(participates);
            s = studentRepository.save(s);
        }
        return t;
    }

    public void insertCourses() {
        course1 = createCourse(Set.of(uDirector1, uDirector2), "Wirtschaftsinformatik");
        course1 = createCourse(Set.of(uDirector3), "Maschinenbau");
    }

    private Course createCourse(Set<Director> directors, String title) {
        Course c = new Course();
        c.setLeadBy(directors);
        c.setTitle(title);
        return courseRepository.save(c);
    }

    private void insertSpecialisationCourses() {
        specialisationCourseSE = createSpecialisationCourse(course1, "Software Engineering");
        specialisationCourseSC = createSpecialisationCourse(course1, "Sales and Consulting");
        specialisationCourseAMB = createSpecialisationCourse(course1, "Allgemeiner Maschinenbau");
    }

    private SpecialisationCourse createSpecialisationCourse(Course course, String title) {
        SpecialisationCourse specialisationCourse = new SpecialisationCourse();
        specialisationCourse.setCourse(course);
        specialisationCourse.setTitle(title);
        return specialisationCourseRepository.save(specialisationCourse);
    }

}
