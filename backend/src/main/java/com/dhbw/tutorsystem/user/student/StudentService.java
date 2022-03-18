package com.dhbw.tutorsystem.user.student;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    public Student getLoggedInStudent() {
        Object objectPrincipal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (objectPrincipal instanceof UserDetails) {
            return studentRepository.findByEmail(((UserDetails) objectPrincipal).getUsername()).orElse(null);
        } else {
            return null;
        }
    }
}