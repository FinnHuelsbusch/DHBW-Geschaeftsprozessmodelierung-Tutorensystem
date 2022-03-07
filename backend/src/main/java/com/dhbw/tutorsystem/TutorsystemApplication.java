package com.dhbw.tutorsystem;

import java.time.LocalDate;
import java.util.HashSet;
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

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class TutorsystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(TutorsystemApplication.class, args);
	}

	@Bean
	public CommandLineRunner init(
			RoleRepository roleRepository,
			UserRepository userRepository,
			PasswordEncoder encoder, 
			DirectorRepository directorRepository, 
			StudentRepository studentRepository, 
			TutorialRepository tutorialRepository) {
		return (args) -> {
			initDatabaseForDevelopment(roleRepository, userRepository, encoder, directorRepository, studentRepository, tutorialRepository);
		};
	}

	private void initDatabaseForDevelopment(RoleRepository roleRepository, UserRepository userRepository,
			PasswordEncoder encoder, DirectorRepository directorRepository, StudentRepository studentRepository, TutorialRepository tutorialRepository) {
		Role rStudent = roleRepository.save(new Role(ERole.ROLE_STUDENT));
		Role rDirector = roleRepository.save(new Role(ERole.ROLE_DIRECTOR));
		Role rAdmin = roleRepository.save(new Role(ERole.ROLE_ADMIN));

		User uAdmin = new User("adam.admin@dhbw-mannheim.de", "1234");
		uAdmin.setRoles(Set.of(rAdmin));
		uAdmin.setPassword(encoder.encode(uAdmin.getPassword()));
		uAdmin.setEnabled(true);
		uAdmin = userRepository.save(uAdmin);

		Director uDirector = new Director("dirk.director@dhbw-mannheim.de", "1234");
		uDirector.setRoles(Set.of(rDirector));
		uDirector.setPassword(encoder.encode(uDirector.getPassword()));
		uDirector.setEnabled(true);
		uDirector = directorRepository.save(uDirector);

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


		HashSet<User> tutors = new HashSet<>(); 
		tutors.add(uStudent1);

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
		tutorial2 = tutorialRepository.save(tutorial2);
	}

}
