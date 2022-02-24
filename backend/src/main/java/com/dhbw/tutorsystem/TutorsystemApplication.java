package com.dhbw.tutorsystem;

import com.dhbw.tutorsystem.role.ERole;
import com.dhbw.tutorsystem.role.Role;
import com.dhbw.tutorsystem.role.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TutorsystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(TutorsystemApplication.class, args);
	}

	@Bean
	public CommandLineRunner init(
			RoleRepository roleRepository
	) {
		return (args) -> {
			initDatabaseForDevelopment(roleRepository);
		};
	}

	private void initDatabaseForDevelopment(RoleRepository roleRepository) {
		Role rStudent = roleRepository.save(new Role(ERole.ROLE_STUDENT));
		Role rDirector = roleRepository.save(new Role(ERole.ROLE_DIRECTOR));
	}

}
