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
import com.dhbw.tutorsystem.tutorial.CreateTutorialRequest;
import com.dhbw.tutorsystem.tutorial.Tutorial;
import com.dhbw.tutorsystem.tutorial.TutorialRepository;
import com.dhbw.tutorsystem.tutorialRequest.TutorialRequest;
import com.dhbw.tutorsystem.tutorialRequest.TutorialRequestRepository;
import com.dhbw.tutorsystem.user.User;
import com.dhbw.tutorsystem.user.UserRepository;
import com.dhbw.tutorsystem.user.director.Director;
import com.dhbw.tutorsystem.user.director.DirectorRepository;
import com.dhbw.tutorsystem.user.student.Student;
import com.dhbw.tutorsystem.user.student.StudentRepository;

import org.springframework.beans.factory.annotation.Autowired;
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
    private final TutorialRequestRepository tutorialRequestRepository;
    private final CourseRepository courseRepository;
    private final SpecialisationCourseRepository specialisationCourseRepository;

    private User uAdmin;
    private Director uDirector1, uDirector2, uDirector3;
    private Student uStudent1, uStudent2;
    private Student uTutor, uTutor1, uTutor2, uTutor3, uTutor4, uTutor5, uTutor6, uTutor7, uTutor8, uTutor9, uTutor10, uTutor11, uTutor12, uTutor13, uTutor14, uTutor15;

    private Course course1, course2, course3;
    private SpecialisationCourse specialisationCourseSE, specialisationCourseSC, specialisationCourseAMB, specialisationCourseAI, specialisationCourseCS, specialisationCourseDBM, specialisationCourseFDL;

    public void initDatabaseForDevelopment() {
        // ordering of these methods is important, because of existing dependencies
        // between entities
        insertUsers();
        insertCoursesWithSpecialisation();
        insertTutorials();
    }

    public void insertUsers() {
        Role rStudent = roleRepository.save(new Role(ERole.ROLE_STUDENT));
        Role rDirector = roleRepository.save(new Role(ERole.ROLE_DIRECTOR));
        Role rAdmin = roleRepository.save(new Role(ERole.ROLE_ADMIN));

        uAdmin = new User("adam.admin@dhbw-mannheim.de", "1234");
        uAdmin.setRoles(Set.of(rAdmin));
        uAdmin.setPassword(encoder.encode(uAdmin.getPassword()));
        uAdmin.setEnabled(true);
        uAdmin.setLastPasswordAction(LocalDateTime.now());
        uAdmin.setFirstName("Adam");
        uAdmin.setLastName("Admin");
        uAdmin = userRepository.save(uAdmin);

        uDirector1 = new Director("dirk.director@dhbw-mannheim.de", "1234");
        uDirector1.setRoles(Set.of(rDirector));
        uDirector1.setPassword(encoder.encode(uDirector1.getPassword()));
        uDirector1.setEnabled(true);
        uDirector1.setLastPasswordAction(LocalDateTime.now());
        uDirector1.setFirstName("Dirk");
        uDirector1.setLastName("Director");
        uDirector1 = directorRepository.save(uDirector1);

        uDirector2 = new Director("daniel.director@dhbw-mannheim.de", "1234");
        uDirector2.setRoles(Set.of(rDirector));
        uDirector2.setPassword(encoder.encode(uDirector2.getPassword()));
        uDirector2.setEnabled(true);
        uDirector2.setLastPasswordAction(LocalDateTime.now());
        uDirector2.setFirstName("Daniel");
        uDirector2.setLastName("Director");
        uDirector2 = directorRepository.save(uDirector2);

        uDirector3 = new Director("doris.director@dhbw-mannheim.de", "1234");
        uDirector3.setRoles(Set.of(rDirector));
        uDirector3.setPassword(encoder.encode(uDirector3.getPassword()));
        uDirector3.setEnabled(true);
        uDirector3.setLastPasswordAction(LocalDateTime.now());
        uDirector3.setFirstName("Doris");
        uDirector3.setLastName("Director");
        uDirector3 = directorRepository.save(uDirector3);

        uStudent1 = new Student("s111111@student.dhbw-mannheim.de", "1234");
        uStudent1.setRoles(Set.of(rStudent));
        uStudent1.setPassword(encoder.encode(uStudent1.getPassword()));
        uStudent1.setEnabled(true);
        uStudent1.setFirstName("Leon");
        uStudent1.setLastName("Bauer");
        uStudent1 = studentRepository.save(uStudent1);

        uStudent2 = new Student("s222222@student.dhbw-mannheim.de", "1234");
        uStudent2.setRoles(Set.of(rStudent));
        uStudent2.setPassword(encoder.encode(uStudent2.getPassword()));
        uStudent2.setEnabled(true);
        uStudent2.setFirstName("Elon");
        uStudent2.setLastName("Musk");
        uStudent2 = studentRepository.save(uStudent2);

        uTutor = new Student("s000002@student.dhbw-mannheim.de", "1234");
        uTutor.setFirstName("Amanda");
        uTutor.setLastName("Huginkiss");
        uTutor.setRoles(Set.of(rStudent));
        uTutor.setPassword(encoder.encode(uTutor.getPassword()));
        uTutor.setEnabled(true);
        uTutor.setLastPasswordAction(LocalDateTime.now());
        uTutor = studentRepository.save(uTutor);

        uTutor1 = new Student("s000003@student.dhbw-mannheim.de", "1234");
        uTutor1.setFirstName("Tim");
        uTutor1.setLastName("Cook");
        uTutor1.setRoles(Set.of(rStudent));
        uTutor1.setPassword(encoder.encode(uTutor1.getPassword()));
        uTutor1.setEnabled(true);
        uTutor1.setLastPasswordAction(LocalDateTime.now());
        uTutor1 = studentRepository.save(uTutor1);

        uTutor2 = new Student("s000004@student.dhbw-mannheim.de", "1234");
        uTutor2.setFirstName("Mark");
        uTutor2.setLastName("Zuckerberg");
        uTutor2.setRoles(Set.of(rStudent));
        uTutor2.setPassword(encoder.encode(uTutor2.getPassword()));
        uTutor2.setEnabled(true);
        uTutor2.setLastPasswordAction(LocalDateTime.now());
        uTutor2 = studentRepository.save(uTutor2);

        uTutor3 = new Student("s000005@student.dhbw-mannheim.de", "1234");
        uTutor3.setFirstName("Sundar");
        uTutor3.setLastName("Pichai");
        uTutor3.setRoles(Set.of(rStudent));
        uTutor3.setPassword(encoder.encode(uTutor3.getPassword()));
        uTutor3.setEnabled(true);
        uTutor3.setLastPasswordAction(LocalDateTime.now());
        uTutor3 = studentRepository.save(uTutor3);

        uTutor4 = new Student("s000006@student.dhbw-mannheim.de", "1234");
        uTutor4.setFirstName("Steve");
        uTutor4.setLastName("Jobs");
        uTutor4.setRoles(Set.of(rStudent));
        uTutor4.setPassword(encoder.encode(uTutor4.getPassword()));
        uTutor4.setEnabled(true);
        uTutor4.setLastPasswordAction(LocalDateTime.now());
        uTutor4 = studentRepository.save(uTutor4);

        uTutor5 = new Student("s000007@student.dhbw-mannheim.de", "1234");
        uTutor5.setFirstName("Christian");
        uTutor5.setLastName("Klein");
        uTutor5.setRoles(Set.of(rStudent));
        uTutor5.setPassword(encoder.encode(uTutor5.getPassword()));
        uTutor5.setEnabled(true);
        uTutor5.setLastPasswordAction(LocalDateTime.now());
        uTutor5 = studentRepository.save(uTutor5);

        uTutor6 = new Student("s000008@student.dhbw-mannheim.de", "1234");
        uTutor6.setFirstName("Brad");
        uTutor6.setLastName("Pitt");
        uTutor6.setRoles(Set.of(rStudent));
        uTutor6.setPassword(encoder.encode(uTutor6.getPassword()));
        uTutor6.setEnabled(true);
        uTutor6.setLastPasswordAction(LocalDateTime.now());
        uTutor6 = studentRepository.save(uTutor6);

        uTutor7 = new Student("s000009@student.dhbw-mannheim.de", "1234");
        uTutor7.setFirstName("Leeroy");
        uTutor7.setLastName("Jenkins");
        uTutor7.setRoles(Set.of(rStudent));
        uTutor7.setPassword(encoder.encode(uTutor7.getPassword()));
        uTutor7.setEnabled(true);
        uTutor7.setLastPasswordAction(LocalDateTime.now());
        uTutor7 = studentRepository.save(uTutor7);

        uTutor8 = new Student("s000010@student.dhbw-mannheim.de", "1234");
        uTutor8.setFirstName("Thorsten");
        uTutor8.setLastName("Borsten");
        uTutor8.setRoles(Set.of(rStudent));
        uTutor8.setPassword(encoder.encode(uTutor8.getPassword()));
        uTutor8.setEnabled(true);
        uTutor8.setLastPasswordAction(LocalDateTime.now());
        uTutor8 = studentRepository.save(uTutor8);

        uTutor9 = new Student("s000011@student.dhbw-mannheim.de", "1234");
        uTutor9.setFirstName("Deez");
        uTutor9.setLastName("Nuts");
        uTutor9.setRoles(Set.of(rStudent));
        uTutor9.setPassword(encoder.encode(uTutor9.getPassword()));
        uTutor9.setEnabled(true);
        uTutor9.setLastPasswordAction(LocalDateTime.now());
        uTutor9 = studentRepository.save(uTutor9);

        uTutor10 = new Student("s000012@student.dhbw-mannheim.de", "1234");
        uTutor10.setFirstName("Gayview");
        uTutor10.setLastName("Mahat");
        uTutor10.setRoles(Set.of(rStudent));
        uTutor10.setPassword(encoder.encode(uTutor10.getPassword()));
        uTutor10.setEnabled(true);
        uTutor10.setLastPasswordAction(LocalDateTime.now());
        uTutor10 = studentRepository.save(uTutor10);

        uTutor11 = new Student("s000013@student.dhbw-mannheim.de", "1234");
        uTutor11.setFirstName("Gavit");
        uTutor11.setLastName("Awaii");
        uTutor11.setRoles(Set.of(rStudent));
        uTutor11.setPassword(encoder.encode(uTutor11.getPassword()));
        uTutor11.setEnabled(true);
        uTutor11.setLastPasswordAction(LocalDateTime.now());
        uTutor11 = studentRepository.save(uTutor11);

        uTutor12 = new Student("s000014@student.dhbw-mannheim.de", "1234");
        uTutor12.setFirstName("Sumwun");
        uTutor12.setLastName("Speshal");
        uTutor12.setRoles(Set.of(rStudent));
        uTutor12.setPassword(encoder.encode(uTutor12.getPassword()));
        uTutor12.setEnabled(true);
        uTutor12.setLastPasswordAction(LocalDateTime.now());
        uTutor12 = studentRepository.save(uTutor12);

        uTutor13 = new Student("s000015@student.dhbw-mannheim.de", "1234");
        uTutor13.setFirstName("Mike");
        uTutor13.setLastName("Lituris");
        uTutor13.setRoles(Set.of(rStudent));
        uTutor13.setPassword(encoder.encode(uTutor13.getPassword()));
        uTutor13.setEnabled(true);
        uTutor13.setLastPasswordAction(LocalDateTime.now());
        uTutor13 = studentRepository.save(uTutor13);

        uTutor14 = new Student("s000016@student.dhbw-mannheim.de", "1234");
        uTutor14.setFirstName("Mike");
        uTutor14.setLastName("Rodge");
        uTutor14.setRoles(Set.of(rStudent));
        uTutor14.setPassword(encoder.encode(uTutor14.getPassword()));
        uTutor14.setEnabled(true);
        uTutor14.setLastPasswordAction(LocalDateTime.now());
        uTutor14 = studentRepository.save(uTutor14);

        uTutor15 = new Student("s000017@student.dhbw-mannheim.de", "1234");
        uTutor15.setFirstName("Hugh");
        uTutor15.setLastName("Jass");
        uTutor15.setRoles(Set.of(rStudent));
        uTutor15.setPassword(encoder.encode(uTutor15.getPassword()));
        uTutor15.setEnabled(true);
        uTutor15.setLastPasswordAction(LocalDateTime.now());
        uTutor15 = studentRepository.save(uTutor15);
    }

    public void insertTutorials() {
        createTutorial(
            "Mathe I", 
            "Wöchentlich jeden Dienstag um 18 Uhr", 
            "In diesem Tutorium üben wir mit Euch grundlegende Analysis Rechenarten.
            Gemeinsam erarbeiten wir uns die Grundlagen, errechnen Beispielaufgaben und führen Klausurvorbereitungen durch.
            WICHTIG: Das Tutorium findet aktuell trotz Pandemie in Präsenz statt! Bitte tragt während des gesamten Tutoriums eine FFP2-Maske und bringt ein aktuelles, negatives Testergebnis mit!",
            120,
            LocalDate.now(), 
            LocalDate.now().plusWeeks(2), 
            Set.of(uTutor, tutor3), 
            Set.of(uStudent1, tutor4, tutor5, tutor6, tutor7), 
            Set.of(specialisationCourseSE, specialisationCourseSC)
        );

        createTutorial(
            "Elektrotechnik 1", 
            "Wir bieten während der Vorlesungszeit, mit einem Team bestehend aus drei Tutoren, wöchentlich mehrere Tutorien an. Hier können alle Studienanfänger in kleinen Gruppen Probleme des Studiums und vieles mehr besprechen. Der Terminplan wird zu Beginn des Semesters über den Mailverteiler geschickt und hängt auch im Schaukasten neben der Fachschaft aus.",
            "Die Tutoren der Fachschaft Elektrotechnik wollen Dir den Studieneinstieg erleichtern. Dazu bieten wir fachliche Unterstützung für Vorlesungen und Übungen der ersten beiden Semester an und helfen bei Fragen rund ums Studium und der Uni weiter.", 
            120,
            LocalDate.now(), 
            LocalDate.now().plusWeeks(9), 
            Set.of(uTutor), 
            Set.of(uStudent1), 
            Set.of(specialisationCourseAMB)
        );

        createTutorial(
            "Elektrotechnik 2", 
            "Jeden zweiten Montag um 19 Uhr",
            "Das vorlesungsbegleitende Tutorium soll Ihnen die Möglichkeit bieten, die in der Vorlesung, im Hörsaallabor und in der Übung erworbenen Kenntnisse durch selbstständiges Berechnen zu vertiefen. Zur Beantwortung von Fragen zu den Aufgaben und zur Vorlesung, stehen wir Tutoren Ihnen gerne zur Verfügung.", 
            120,
            LocalDate.now().plusWeeks(1), 
            LocalDate.now().plusWeeks(10), 
            Set.of(uTutor),
            Set.of(uStudent1), 
            Set.of(specialisationCourseAMB)
        );

        createTutorial(
            "Elektrotechnik 3", 
            "Auf Anfrage", 
            "Diese Veranstaltung dient als Basispraktikum und der Nachbereitung des Vorlesungsstoffes auf interaktiver Weise. Individuelle Fragen sind ausdrücklich erwünscht.

            Hinweis: Keine Testate, keine Übungszettel, keine Scheine, keine Leistungspunkte! Die Teilnahme ist freiwillig.",
            120,
            LocalDate.now().plusWeeks(2), 
            LocalDate.now().plusWeeks(11), 
            Set.of(uTutor),
            Set.of(uStudent1), 
            Set.of(specialisationCourseAMB)
        );

        createTutorial(
            "Elektrotechnik 4", 
            "Auf Anfrage",
            "Das vorlesungsbegleitende Tutorium soll Ihnen die Möglichkeit bieten, die in der Vorlesung, 
            im Hörsaallabor und in der Übung erworbenen Kenntnisse durch selbstständiges Berechnen 
            zu vertiefen. Zur Beantwortung von Fragen zu den Aufgaben und zur Vorlesung, stehen wir 
            Tutoren Ihnen gerne zur Verfügung.", 
            120,
            LocalDate.now().plusWeeks(3), 
            LocalDate.now().plusWeeks(12), 
            Set.of(uTutor),
            Set.of(uStudent1), 
            Set.of(specialisationCourseAMB)
        );

        createTutorial(
            "Mathe 1",
            "Jeden Dienstag von 18 bis 19 Uhr.",
            "Das Tutorium Mathematik findet in jedem Semester statt.

            Im WS liegt der Schwerpunkt des Tutorium bei Mathematische Grundlagen, im SS bei Analysis 1 und Lineare Algebra 1.
            
            Das Tutorium Mathematik richtet sich an alle Studierenden, die die Prüfung zum Modul Mathematische Grundlagen (WS) bzw. Analysis 1 und/ oder Lineare Algebra 1 (SS) nicht bestanden haben oder ihre Kenntnisse vertiefen möchten.
            
            
            Das Tutorium findet im SS20 mittwochs 08:15 Uhr bis 09.45 Uhr als Online-Veranstaltung statt.",
            120,
            LocalDate.now().plusWeeks(3),
            LocalDate.now().plusDays(21),
            Set.of(uTutor),
            Set.of(uStudent1, uStudent2),
            Set.of(specialisationCourseSE, specialisationCourseSC)
        );

        createTutorial(
            "Programmieren 1",
            "Jeden Donnerstag von 18 bis 19 Uhr.",
            "Das Vorlesungs- und Übungsmaterial darf ausschließlich zu Zwecken genutzt werden, die in direktem Zusammenhang mit der Veranstaltung Programmieren stehen. Insbesondere die Weitergabe des hier zur Verfügung gestellten Materials an Dritte ist nicht gestattet. Foliensätze, Übungsblätter, Musterlösungen und hier veröffentlichter Java-Code unterliegen dem Copyright des Lehrstuhls Programmierparadigmen (IPD Snelting).",
            120,
            LocalDate.now().plusWeeks(3),
            LocalDate.now().plusWeeks(12),
            Set.of(uTutor),
            Set.of(uStudent1),
            Set.of(specialisationCourseSE, specialisationCourseSC)
        );
    }

    private void insertTutorialRequests(){
        createTutorielRequest(
            Set.of(specialisationCourseSE),
            "Hey, ich bin gerade im 3. Semester und habe Finanzbuchhaltung bei Herr Heiduk. Ich habe meine Schwierigkeiten mit den Grundlagen und der korrekten Buchung von Geschäftsfällen. Brauche dringend Unterstützung!",
            "Tutoriums-Anfrage für Finanzbuchhaltung",
            Set.of(uTutor10),
            uTutor14
        );

        createTutorielRequest(
            Set.of(specialisationCourseSE),
            "Hallo, ich bin im vierten Semester. Ich habe Herr Pagnia in Verteilte Systeme. Seine Folien sind sehr umfangreich aber ich komme bei dem Stoff nicht wirklich mit und brächte deshalb unbedingt Hilde bei seinen Beispielaufgaben.",
            "Tutoriums-Anfrage für Verteilte Systeme",
            Set.of(uTutor1),
            uTutor15
        );
        
        createTutorielRequest(
            Set.of(specialisationCourseSE),
            "Hey, ich bin gerade im 2. Semester und habe Probleme bei dem Modul Betriebs- und Kommunikationssysteme. Die Klausur steht in zwei Wochen an und ich verstehe die Beispielaufgaben aus dem Skript nicht.",
            "Tutoriums-Anfrage für Betriebs- und Kommunikationssysteme",
            Set.of(uTutor6),
            uTutor9
        );
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

    private TutorialRequest createTutorielRequest(Set<SpecialisationCourse> course, String description, String title, Set<Student> interestedStudents, Student createdBy){
        TutorialRequest tutorialRequest = new TutorialRequest();
        tutorialRequest.setSpecialisationCourses(course);
        tutorialRequest.setDescription(description);
        tutorialRequest.setTitle(title);
        tutorialRequest.setInterestedStudents(interestedStudents);
        tutorialRequest.setCreatedBy(createdBy);

        tutorialRequest = tutorialRequestRepository.save(tutorialRequest);
        return tutorialRequest;
    }

    public void insertCoursesWithSpecialisation() {
        course1 = createCourse(Set.of(uDirector1, uDirector2), "Wirtschaftsinformatik", "WI");
        specialisationCourseSE = createSpecialisationCourse(course1, "Software Engineering", "SE");
        specialisationCourseSC = createSpecialisationCourse(course1, "Sales and Consulting", "SC");
        course1.setSpecialisationCourses(Set.of(specialisationCourseSE, specialisationCourseSC));
        course1 = courseRepository.save(course1);

        course2 = createCourse(Set.of(uDirector1), "Informatik", "INF");
        specialisationCourseAI = createSpecialisationCourse(course2, "Angewandte Informatik", "AI");
        specialisationCourseCS = createSpecialisationCourse(course2, "Cyber Security", "CS");
        course2.setSpecialisationCourses(Set.of(specialisationCourseAI, specialisationCourseCS));
        course2 = courseRepository.save(course2);


        course3 = createCourse(Set.of(uDirector1, uDirector2), "Betriebswirtschaftslehre", "BWL");
        specialisationCourseDBM = createSpecialisationCourse(course1, "Digital Business Management", "DBM");
        specialisationCourseFDL = createSpecialisationCourse(course1, "Finanzdienstleistungen", "FDL");
        specialisationCourseAMB = createSpecialisationCourse(course2, "Allgemeiner Maschinenbau", "AMB");
        course3.setSpecialisationCourses(Set.of(specialisationCourseDBM, specialisationCourseFDL));
        course3 = courseRepository.save(course3);
    }

    private Course createCourse(Set<Director> directors, String title, String abbreviation) {
        Course c = new Course();
        c.setLeadBy(directors);
        c.setTitle(title);
        c.setAbbreviation(abbreviation);
        return courseRepository.save(c);
    }

    private SpecialisationCourse createSpecialisationCourse(Course course, String title, String abbreviation) {
        SpecialisationCourse specialisationCourse = new SpecialisationCourse();
        specialisationCourse.setCourse(course);
        specialisationCourse.setTitle(title);
        specialisationCourse.setAbbreviation(abbreviation);
        return specialisationCourseRepository.save(specialisationCourse);
    }
}
