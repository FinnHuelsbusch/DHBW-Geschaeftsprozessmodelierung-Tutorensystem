package com.dhbw.tutorsystem.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.dhbw.tutorsystem.role.ERole;
import com.dhbw.tutorsystem.role.Role;
import com.dhbw.tutorsystem.role.RoleRepository;
import com.dhbw.tutorsystem.security.authentication.exception.InvalidUserTypeException;
import com.dhbw.tutorsystem.security.authentication.exception.RoleNotFoundException;
import com.dhbw.tutorsystem.user.director.Director;
import com.dhbw.tutorsystem.user.director.DirectorRepository;
import com.dhbw.tutorsystem.user.student.Student;
import com.dhbw.tutorsystem.user.student.StudentRepository;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final DirectorRepository directorRepository;
    private final RoleRepository roleRepository;

    public User getLoggedInUser() {
        Object objectPrincipal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (objectPrincipal instanceof UserDetails) {
            return userRepository.findByEmail(((UserDetails) objectPrincipal).getUsername()).orElse(null);
        } else {
            return null;
        }
    }

    public Role getUserRole(String userMail) throws RoleNotFoundException {
        // get role by email
        Optional<Role> role = Optional.empty();
        if (User.isStudentMail(userMail)) {
            role = roleRepository.findByName(ERole.ROLE_STUDENT);
        } else if (User.isDirectorMail(userMail)) {
            role = roleRepository.findByName(ERole.ROLE_DIRECTOR);
        }
        if (role.isPresent()) {
            return role.get();
        } else {
            // role might not be available in DB
            throw new RoleNotFoundException();
        }
    }

    public List<User> saveAllUserSubtypes(List<User> users) {
        List<User> savedUsers = new ArrayList<>();
        for (User user : users) {
            savedUsers.add(saveUserSubtype(user));
        }
        return savedUsers;
    }

    // saves a user in the correct repo
    public User saveUserSubtype(User user) throws InvalidUserTypeException {
        // check the type of the provided user
        if (user instanceof Student) {
            return studentRepository.save((Student) user);
        } else if (user instanceof Director) {
            return directorRepository.save((Director) user);
        } else {
            throw new InvalidUserTypeException();
        }
    }
}