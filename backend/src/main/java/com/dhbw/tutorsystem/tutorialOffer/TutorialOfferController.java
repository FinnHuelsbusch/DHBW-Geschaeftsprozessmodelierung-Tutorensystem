package com.dhbw.tutorsystem.tutorialOffer;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import javax.management.RuntimeErrorException;
import javax.validation.Valid;

import com.dhbw.tutorsystem.user.User;
import com.dhbw.tutorsystem.user.UserRepository;
import com.dhbw.tutorsystem.user.UserService;
import com.fasterxml.jackson.annotation.JsonFormat;

import org.hibernate.annotations.SourceType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.server.ResponseStatusException;


import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class TutorialOfferController {

    private final TutorialOfferRepository tutorialOfferRepository;
    private final UserService userService; 
    
    @PostMapping("/tutorialoffer")
    public ResponseEntity<Void> createTutorialOffer(@RequestBody TutorialOffer tutorialOffer) {
        
        // find out which user executes this operation
        User user = userService.getLoggedInUser(); 
        if(user != null){
            tutorialOffer.setUser(user);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED); 
        }
        
        tutorialOfferRepository.save(tutorialOffer);
        
        return  new ResponseEntity<Void>(HttpStatus.OK);
    }
}
