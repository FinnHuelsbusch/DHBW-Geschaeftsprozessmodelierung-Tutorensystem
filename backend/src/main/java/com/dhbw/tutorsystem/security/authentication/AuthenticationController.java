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
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.Valid;
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

    @PostMapping("/signin")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(roles, jwt, jwtUtils.getExpirationDateFromJwtToken(jwt),
                userDetails.getUserId(), userDetails.getEmailAddress()));
    }

    @GetMapping("/signup/enable")
    public ResponseEntity<?> enableUserAccount(@RequestParam(name = "h") String hash) {
        return ResponseEntity.ok("Enabled");
    }

    @PostMapping("/signup/{hash}")
    public ResponseEntity<?> registerWithPassword(@Valid @RequestBody SignupRequest signupRequest) {
        return ResponseEntity.ok("");
    }

    @PostMapping("/signup")
    public ResponseEntity<?> register(@Valid @RequestBody SignupRequest signUpRequest) {
        // 1. receive email & password
        // 2. save user with lastPasswordAction, enabled = false
        // 3. hash lastPasswordAction and email to form link /register/hash
        // 4. check account /signup/enable?h=hash
        // 5. reroute to login OR create JWT

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Email existiert bereits");
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
            return ResponseEntity.badRequest().body("Email ist ungültig");
        }
        if (!role.isPresent()) {
            return ResponseEntity.badRequest().body("Keine zugehörige Nutzerrolle gefunden");
        }
        user.setRoles(Set.of(role.get()));
        userRepository.save(user);

        // issue a token so that the user is directly logged in, using the plain text
        // password
        JwtResponse jwtResponse = (JwtResponse) login(
                new LoginRequest(user.getEmail(), signUpRequest.getPassword()))
                        .getBody();

        return ResponseEntity.ok(new SignupResponse(user.getEmail(), jwtResponse.getToken()));
    }
}
