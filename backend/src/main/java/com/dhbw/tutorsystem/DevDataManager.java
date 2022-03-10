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

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DevDataManager {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final DirectorRepository directorRepository;
    private final StudentRepository studentRepository;
    private final TutorialRepository tutorialRepository;
    private final CourseRepository courseRepository; 
    private final SpecialisationCourseRepository specialisationCourseRepository; 


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

        HashSet<Director> directors = new HashSet<>(); 
		directors.add(uDirector1);
        directors.add(uDirector2);

        Course course1 = new Course(); 
        course1.setLeadBy(directors);
        course1.setTitle("Wirtschaftsinformatik");
        course1 = courseRepository.save(course1);

        Course course2 = new Course(); 
        course2.setLeadBy(Set.of(uDirector3));
        course2.setTitle("Maschinenbau");
        course2 = courseRepository.save(course2);
        

        SpecialisationCourse specialisationCourseSE = new SpecialisationCourse(); 
        specialisationCourseSE.setCourse(course1);
        specialisationCourseSE.setTitle("Software Engineering");
        specialisationCourseSE = specialisationCourseRepository.save(specialisationCourseSE);

        SpecialisationCourse specialisationCourseSC = new SpecialisationCourse(); 
        specialisationCourseSC.setCourse(course1);
        specialisationCourseSC.setTitle("Sales and Consulting");
        specialisationCourseSC = specialisationCourseRepository.save(specialisationCourseSC);

        SpecialisationCourse specialisationCourseAMB = new SpecialisationCourse(); 
        specialisationCourseAMB.setCourse(course1);
        specialisationCourseAMB.setTitle("Allgemeiner Maschinenbau");
        specialisationCourseAMB = specialisationCourseRepository.save(specialisationCourseAMB);

        HashSet<User> tutors = new HashSet<>(); 
		tutors.add(uStudent1);

		HashSet<SpecialisationCourse> specialisationCourses1 = new HashSet<>(); 
		specialisationCourses1.add(specialisationCourseSC); 
        specialisationCourses1.add(specialisationCourseSE);

        course1.setSpecialisationCourses(specialisationCourses1);
        course1 = courseRepository.save(course1);

        course2.setSpecialisationCourses(Set.of(specialisationCourseAMB));
        course2 = courseRepository.save(course2);

		HashSet<Student> participants = new HashSet<>(); 
		participants.add(uStudent2); 

		Tutorial tutorial1 = new Tutorial(); 
		tutorial1.setAppointment("Jeden Dienstag von 18 bis 19 Uhr.");
		tutorial1.setDescription("Mathe für alle, die den ersten Versuch nicht bestanden haben. Schwerpunkt Analysis.");
		tutorial1.setDurationMinutes(120);
		tutorial1.setTitle("Mathe 1");
		tutorial1.setTutors(tutors);
		tutorial1.setStart(LocalDate.now());
		tutorial1.setEnd(LocalDate.now().plusDays(21));
		tutorial1.setParticipants(participants);
        tutorial1.setSpecialisationCourses(specialisationCourses1);
		tutorial1 = tutorialRepository.save(tutorial1);


		Tutorial tutorial2 = new Tutorial(); 
		tutorial2.setAppointment("Jeden Dienstag von 18 bis 19 Uhr.");
		tutorial2.setDescription("Aufgaben im Bereich programmieren mit Java. Schwerpunkt Objektorientierung");
		tutorial2.setDurationMinutes(120);
		tutorial2.setTitle("Programmieren 1");
		tutorial2.setTutors(tutors);
		tutorial2.setStart(LocalDate.now().plusWeeks(4));
		tutorial2.setEnd(LocalDate.now().plusWeeks(12));
		tutorial2.setParticipants(participants);
        tutorial2.setSpecialisationCourses(specialisationCourses1);
		tutorial2 = tutorialRepository.save(tutorial2);

    }

}
