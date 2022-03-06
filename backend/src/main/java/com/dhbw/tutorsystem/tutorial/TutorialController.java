package com.dhbw.tutorsystem.tutorial;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.dhbw.tutorsystem.exception.TSBadRequestException;
import com.dhbw.tutorsystem.exception.TSExceptionResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/tutorial")
@AllArgsConstructor
@SecurityScheme(name = "jwt-auth", type = SecuritySchemeType.HTTP, scheme = "bearer")
public class TutorialController {

    private final TutorialRepository tutorialRepository; 

    @Operation(
        tags={"tutorial"},
        summary = "Get all tutorials.", 
        description = "Get all tutorials. ",
        security = @SecurityRequirement(name = "jwt-auth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns all Tutorials orderd and filterd."),
    })
    @GetMapping(params = {"pageNumber","pageSize","sortBy"})
    public ResponseEntity<List<Tutorial>> getAllTutorials(
        @RequestParam(defaultValue = "0") Integer pageNumber, 
        @RequestParam(defaultValue = "10") Integer pageSize,
        @RequestParam(defaultValue = "id") String sortBy
    ){
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
 
        Page<Tutorial> pagedResult = tutorialRepository.findAll(paging);
         
        if(pagedResult.hasContent()) {
            return new ResponseEntity<List<Tutorial>>(pagedResult.getContent(), HttpStatus.OK); 
        } else {
            return new ResponseEntity<List<Tutorial>>(new ArrayList<Tutorial>(), HttpStatus.OK); 
        }

 
        
    }

    @Operation(
        tags={"tutorial"},
        summary = "Get one specific tutorials.", 
        description = "Get one specific tutorial by a tutorial ID. "
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns the tutorial."),
            @ApiResponse(responseCode = "400", description = "A tutorial with the given ID does not exist", content = @Content(schema = @Schema(implementation = TSExceptionResponse.class)))

        })
    @GetMapping(params = {"ID"})
    public ResponseEntity<Tutorial> getTutorial(
        @RequestParam Integer ID
    ){
        Optional<Tutorial> optionalTutorial =  tutorialRepository.findById(ID); 
        if(optionalTutorial.isEmpty())
        {
            throw new TSBadRequestException("The tutorial with the given ID does not exist");
        }else{
            return new ResponseEntity<>(optionalTutorial.get(), HttpStatus.OK); 
        }
 
        
    }
}
