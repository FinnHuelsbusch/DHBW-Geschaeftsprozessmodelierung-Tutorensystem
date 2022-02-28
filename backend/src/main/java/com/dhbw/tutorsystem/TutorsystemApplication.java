package com.dhbw.tutorsystem;

import java.util.Set;

import com.dhbw.tutorsystem.role.ERole;
import com.dhbw.tutorsystem.role.Role;
import com.dhbw.tutorsystem.role.RoleRepository;
import com.dhbw.tutorsystem.user.User;
import com.dhbw.tutorsystem.user.UserRepository;

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
			PasswordEncoder encoder) {
		return (args) -> {
			initDatabaseForDevelopment(roleRepository, userRepository, encoder);
		};
	}

	private void initDatabaseForDevelopment(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder encoder) {
		Role rStudent = roleRepository.save(new Role(ERole.ROLE_STUDENT));
		Role rDirector = roleRepository.save(new Role(ERole.ROLE_DIRECTOR));
		Role rAdmin = roleRepository.save(new Role(ERole.ROLE_ADMIN));

		User uAdmin = new User("sAdmin@dhbw-mannheim.de", "1234");
		uAdmin.setRoles(Set.of(rAdmin));
		uAdmin.setPassword(encoder.encode(uAdmin.getPassword()));
		uAdmin = userRepository.save(uAdmin);

		User uDirector = new User("sDirector@dhbw-mannheim.de", "1234");
		uDirector.setRoles(Set.of(rDirector));
		uDirector.setPassword(encoder.encode(uDirector.getPassword()));
		uDirector = userRepository.save(uDirector);

	}

}
