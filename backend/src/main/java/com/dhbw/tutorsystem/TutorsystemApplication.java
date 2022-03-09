package com.dhbw.tutorsystem;

import java.util.Set;
import java.util.stream.Collectors;

import com.dhbw.tutorsystem.role.RoleRepository;
import com.dhbw.tutorsystem.tutorial.Tutorial;
import com.dhbw.tutorsystem.tutorial.TutorialController;
import com.dhbw.tutorsystem.tutorial.TutorialDto;
import com.dhbw.tutorsystem.tutorial.TutorialRepository;
import com.dhbw.tutorsystem.user.UserRepository;
import com.dhbw.tutorsystem.user.director.DirectorRepository;
import com.dhbw.tutorsystem.user.student.StudentRepository;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
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
	public ModelMapper modelMapper() {
		ModelMapper mapper = new ModelMapper();
		return mapper;
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
			new DevDataManager(roleRepository, userRepository, encoder, directorRepository, studentRepository,
					tutorialRepository)
					.initDatabaseForDevelopment();
		};
	}

}
