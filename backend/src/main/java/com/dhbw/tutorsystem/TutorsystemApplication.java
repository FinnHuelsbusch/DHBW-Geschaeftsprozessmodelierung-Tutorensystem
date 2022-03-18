package com.dhbw.tutorsystem;

import com.dhbw.tutorsystem.course.CourseRepository;
import com.dhbw.tutorsystem.role.RoleRepository;
import com.dhbw.tutorsystem.specialisationCourse.SpecialisationCourseRepository;
import com.dhbw.tutorsystem.tutorial.Tutorial;
import com.dhbw.tutorsystem.tutorial.TutorialRepository;
import com.dhbw.tutorsystem.tutorial.dto.TutorialForDisplay;
import com.dhbw.tutorsystem.user.UserRepository;
import com.dhbw.tutorsystem.user.director.DirectorRepository;
import com.dhbw.tutorsystem.user.student.StudentRepository;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
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
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addMappings(new PropertyMap<Tutorial, TutorialForDisplay>() {
            @Override
            protected void configure() {
                // skip properties to prevent failed conversion
                skip(destination.getNumberOfParticipants());
                skip(destination.isMarked());
                skip(destination.isParticipates());
            }
        });
        return modelMapper;
    }

    @Bean
    public CommandLineRunner init(
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder encoder,
            DirectorRepository directorRepository,
            StudentRepository studentRepository,
            TutorialRepository tutorialRepository,
            CourseRepository courseRepository,
            SpecialisationCourseRepository specialisationCourseRepository) {
        return (args) -> {
            new DevDataManager(roleRepository, userRepository, encoder, directorRepository, studentRepository,
                    tutorialRepository, courseRepository, specialisationCourseRepository)
                    .initDatabaseForDevelopment();
        };
    }

}
