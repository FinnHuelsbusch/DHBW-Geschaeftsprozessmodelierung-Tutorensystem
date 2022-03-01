package com.dhbw.tutorsystem.security.authentication;

import com.dhbw.tutorsystem.mails.EmailSenderService;
import com.dhbw.tutorsystem.role.ERole;
import com.dhbw.tutorsystem.role.Role;
import com.dhbw.tutorsystem.role.RoleRepository;
import com.dhbw.tutorsystem.security.jwt.JwtUtils;
import com.dhbw.tutorsystem.security.services.UserDetailsImpl;
import com.dhbw.tutorsystem.user.User;
import com.dhbw.tutorsystem.user.UserRepository;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.mail.MessagingException;
import javax.validation.Valid;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
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
    final EmailSenderService emailSenderService;

    public AuthenticationController(AuthenticationManager authenticationManager, UserRepository userRepository,
            RoleRepository roleRepository, JwtUtils jwtUtils, PasswordEncoder encoder,
            EmailSenderService emailSenderService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.jwtUtils = jwtUtils;
        this.encoder = encoder;
        this.emailSenderService = emailSenderService;
    }

    @PostMapping("/login")
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

    @PostMapping("/enableAccount")
    public ResponseEntity<?> enableUserAccount(@Valid @RequestBody VerifyRequest verifyRequest) {
        // find user by email and check the hash
        Optional<User> optionalUser = userRepository.findByEmail(verifyRequest.getEmail());
        if (!optionalUser.isPresent() || optionalUser.get().isEnabled()) {
            // intentionally do not give details about why the hash could not be verified
            return ResponseEntity.badRequest().body("");
        }
        User user = optionalUser.get();
        try {
            String hashBase64Expected = createBase64VerificationHash(user.getEmail(), user.getLastPasswordAction());
            if (!StringUtils.equals(hashBase64Expected, verifyRequest.getHash())) {
                return ResponseEntity.badRequest().body("");
            } else {
                return ResponseEntity.ok("Ok");
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        // 1. receive email & password
        // 2. save user with lastPasswordAction, enabled = false
        // 3. hash lastPasswordAction and email to form link /register/hash
        // 4. check account /signup/enable?h=hash
        // 5. reroute to login OR create JWT

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Email existiert bereits");
        }
        // encode user's password, do not save it in plain text
        String encodedPassword = encoder.encode(registerRequest.getPassword());
        User user = new User(registerRequest.getEmail(), encodedPassword);

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
        user.setEnabled(false);
        user.setLastPasswordAction(LocalDateTime.now());

        String hashBase64;
        try {
            hashBase64 = createBase64VerificationHash(user.getEmail(), user.getLastPasswordAction())
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Hashing-Verfahren nicht gefunden");
        }

        userRepository.save(user);

        try {
            emailSenderService.sendRegistrationMail(user.getEmail(), hashBase64);
        } catch (MessagingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Mail konnte nicht versendet werden");
        }

        return ResponseEntity.ok("OK");
    }

    private String createBase64VerificationHash(String email, LocalDateTime timestamp) throws NoSuchAlgorithmException {
        String text = email + timestamp;
        byte[] hash = MessageDigest.getInstance("SHA-256").digest(
                text.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encode(hash).toString();
    }

}
