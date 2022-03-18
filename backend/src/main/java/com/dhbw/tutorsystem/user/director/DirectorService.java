package com.dhbw.tutorsystem.user.director;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class DirectorService {

    private final DirectorRepository directorRepository;

    public Director getLoggedInDirector() {
        Object objectPrincipal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (objectPrincipal instanceof UserDetails) {
            return directorRepository.findByEmail(((UserDetails) objectPrincipal).getUsername()).orElse(null);
        } else {
            return null;
        }
    }
}