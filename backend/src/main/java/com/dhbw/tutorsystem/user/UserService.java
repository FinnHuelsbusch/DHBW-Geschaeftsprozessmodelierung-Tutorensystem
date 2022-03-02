package com.dhbw.tutorsystem.user;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository; 

    public User getLoggedInUser(){
        Object objectPrincipal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(objectPrincipal instanceof UserDetails){
            return userRepository.findByEmail(((UserDetails) objectPrincipal).getUsername()).orElse(null);
        } else {
          return null; 
        }
    }
}