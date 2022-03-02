package com.dhbw.tutorsystem.security.authentication;

import com.dhbw.tutorsystem.role.ERole;
import com.dhbw.tutorsystem.role.Role;
import com.dhbw.tutorsystem.role.RoleRepository;
import com.dhbw.tutorsystem.security.jwt.JwtUtils;
import com.dhbw.tutorsystem.security.services.UserDetailsImpl;
import com.dhbw.tutorsystem.user.User;
import com.dhbw.tutorsystem.user.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/authentication")
public class AuthenticationController {
    final AuthenticationManager authenticationManager;
    final UserRepository userRepository;
    final RoleRepository roleRepository;
    final JwtUtils jwtUtils;
    final PasswordEncoder encoder;

    public AuthenticationController(AuthenticationManager authenticationManager, UserRepository userRepository,
            RoleRepository roleRepository, JwtUtils jwtUtils, PasswordEncoder encoder) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.jwtUtils = jwtUtils;
        this.encoder = encoder;
    }


    // TODO add documentation. 
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(roles, jwt, userDetails.getUserId(), userDetails.getEmailAddress()));
    }

    // TODO add documentation. 
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        // TODO: first send email, then upon email accept: save user in DB

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Error: Email already exists.");
        }
        // encode user's password, do not save it in plain text
        String encodedPassword = encoder.encode(signUpRequest.getPassword());
        User user = new User(signUpRequest.getEmail(), encodedPassword);

        Optional<Role> role = null;
        if (user.isStudentMail()) {
            role = roleRepository.findByName(ERole.ROLE_STUDENT);
        } else if (user.isDirectorMail()) {
            role = roleRepository.findByName(ERole.ROLE_DIRECTOR);
        } else {
            return ResponseEntity.badRequest().body("Error: Email is invalid.");
        }
        if (!role.isPresent()) {
            return ResponseEntity.badRequest().body("Error: No corresponding role could be found.");
        }
        user.setRoles(Set.of(role.get()));
        userRepository.save(user);

        // issue a token so that the user is directly logged in, using the plain text
        // password
        JwtResponse jwtResponse = (JwtResponse) authenticateUser(
                new LoginRequest(user.getEmail(), signUpRequest.getPassword()))
                        .getBody();

        return ResponseEntity.ok(new SignupResponse(user.getEmail(), jwtResponse.getToken()));
    }
}
