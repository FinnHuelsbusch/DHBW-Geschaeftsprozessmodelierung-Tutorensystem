package com.dhbw.tutorsystem.tutorialOffer;

import com.dhbw.tutorsystem.user.User;
import com.dhbw.tutorsystem.user.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.server.ResponseStatusException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@SecurityScheme(name = "jwt-auth", type = SecuritySchemeType.HTTP, scheme = "bearer")
public class TutorialOfferController {

    private final TutorialOfferRepository tutorialOfferRepository;
    private final UserService userService;



    @Operation(
        tags={"tutorialOffer"},
        summary = "Create new TutorialOffer", 
        description = "Creates a new TutorialOffer for the logged in user as tutor.", 
        operationId = "getWithPayloadResponse",
        security = @SecurityRequirement(name = "jwt-auth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Succesful creation"),
    })
    
    @PostMapping("/tutorialoffer")
    public ResponseEntity<Void> createTutorialOffer(@RequestBody TutorialOfferRequest tutorialOfferRequest) {
        TutorialOffer tutorialOffer = new TutorialOffer(); 
        tutorialOffer.setDescription(tutorialOfferRequest.getDescription());
        tutorialOffer.setStart(tutorialOfferRequest.getStart());
        tutorialOffer.setEnd(tutorialOfferRequest.getEnd());
        // find out which user executes this operation
        User user = userService.getLoggedInUser();
        if (user != null) {
            tutorialOffer.setUser(user);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        tutorialOfferRepository.save(tutorialOffer);

        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
