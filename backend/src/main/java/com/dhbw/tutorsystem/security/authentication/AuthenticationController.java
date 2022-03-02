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
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.validation.Valid;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
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
                userDetails.getEmailAddress()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        Optional<User> optionalUser = userRepository.findByEmail(registerRequest.getEmail());
        User user;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
            if (user.isEnabled()) {
                return ResponseEntity.badRequest().body("Email existiert bereits");
            }
            if (Duration.between(user.getLastPasswordAction(), LocalDateTime.now()).toMinutes() < 15) {
                return ResponseEntity.badRequest()
                        .body("Konto wurde bereits registriert, überprüfen Sie zur Aktivierung ihr E-Mail Postfach");
            } else {
                // existing, not-enabled user that re-registered 15min after first registration:
                // update last changed and re-send email
                user.setLastPasswordAction(LocalDateTime.now());
                userRepository.save(user);
            }
        } else {
            // encode user's password, do not save it in plain text
            String encodedPassword = encoder.encode(registerRequest.getPassword());
            user = new User(registerRequest.getEmail(), encodedPassword);

            Optional<Role> role = null;
            if (user.isStudentMail()) {
                role = roleRepository.findByName(ERole.ROLE_STUDENT);
            } else if (user.isDirectorMail()) {
                role = roleRepository.findByName(ERole.ROLE_DIRECTOR);
            } else {
                return ResponseEntity.badRequest().body("Email ist ungültig");
            }
            if (role.isEmpty()) {
                return ResponseEntity.badRequest().body("Keine zugehörige Nutzerrolle gefunden");
            }
            user.setRoles(Set.of(role.get()));
            user.setEnabled(false);
            user.setLastPasswordAction(LocalDateTime.now());
        }

        String hashBase64;
        try {
            hashBase64 = createBase64VerificationHash(user.getEmail(), user.getLastPasswordAction());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Hashing-Verfahren nicht gefunden");
        }
        try {
            emailSenderService.sendRegistrationMail(user.getEmail(), hashBase64);
        } catch (MessagingException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Mail konnte nicht versendet werden");
        }
        userRepository.save(user);

        return ResponseEntity.ok("Ok");
    }

    @PostMapping("/enableAccount")
    public ResponseEntity<?> enableUserAccount(@Valid @RequestBody VerifyRequest verifyRequest) {
        // find user by email and check the hash
        Optional<User> optionalUser = userRepository.findByEmail(verifyRequest.getEmail());
        if (optionalUser.isEmpty() || optionalUser.get().isEnabled()) {
            // intentionally do not give details about why the hash could not be verified
            return ResponseEntity.badRequest().body("");
        }
        User user = optionalUser.get();
        if (isHashClaimValid(verifyRequest.getHash(), user.getEmail(), user.getLastPasswordAction())) {
            // enable user and update the record
            user.setEnabled(true);
            user.setLastPasswordAction(LocalDateTime.now());
            user = userRepository.save(user);
            String jwt = jwtUtils.generateJwtTokenAfterRegistration(user.getEmail());
            return ResponseEntity.ok(new JwtResponse(Role.getRolesString(user.getRoles()), jwt,
                    jwtUtils.getExpirationDateFromJwtToken(jwt),
                    user.getEmail()));
        } else {
            // intentionally do not give details about why the hash could not be verified
            return ResponseEntity.badRequest().body("");
        }
    }

    public boolean isHashClaimValid(String hashClaim, String userEmail, LocalDateTime userLastPasswordAction) {
        try {
            String hashBase64Expected = createBase64VerificationHash(userEmail, userLastPasswordAction);
            return StringUtils.equals(hashBase64Expected, hashClaim);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
    }

    @PostMapping("/requestPasswordReset")
    public ResponseEntity<?> resetPasswordRequest(
            @Valid @RequestBody RequestPasswordResetRequest requestResetPasswordRequest) {
        Optional<User> optionalUser = userRepository.findByEmail(requestResetPasswordRequest.getEmail());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body("Zu dieser E-Mail existiert kein Konto");
        }
        User user = optionalUser.get();
        if (!user.isEnabled()) {
            return ResponseEntity.badRequest()
                    .body("Konto wurde noch nicht aktiviert, bitte prüfen Sie ihr E-Mail Postfach");
        }
        if (Duration.between(user.getLastPasswordAction(), LocalDateTime.now()).toMinutes() < 15) {
            return ResponseEntity.badRequest()
                    .body("Passwort wurde in den letzten 15 Minuten bereits zurückgesetzt");
        }

        String hashBase64;
        try {
            hashBase64 = createBase64VerificationHash(user.getEmail(), user.getLastPasswordAction());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Hashing-Verfahren nicht gefunden");
        }
        try {
            emailSenderService.sendResetPasswordMail(user.getEmail(), hashBase64);
        } catch (MessagingException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Mail konnte nicht versendet werden");
        }
        user.setLastPasswordAction(LocalDateTime.now());
        userRepository.save(user);

        return ResponseEntity.ok("Ok");
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<?> resetUserPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        Optional<User> optionalUser = userRepository.findByEmail(resetPasswordRequest.getEmail());
        if (optionalUser.isEmpty() || optionalUser.get().isEnabled()) {
            // intentionally do not give details about why the hash could not be verified
            return ResponseEntity.badRequest().body("");
        }
        User user = optionalUser.get();
        if (isHashClaimValid(resetPasswordRequest.getHash(), user.getEmail(), user.getLastPasswordAction())) {
            user.setPassword(encoder.encode(resetPasswordRequest.getPassword()));
            user.setLastPasswordAction(LocalDateTime.now());
            return ResponseEntity.ok("");
        } else {
            // intentionally do not give details about why the hash could not be verified
            return ResponseEntity.badRequest().body("");
        }
    }

    private String createBase64VerificationHash(String email, LocalDateTime timestamp) throws NoSuchAlgorithmException {
        String text = email + timestamp;
        byte[] hash = MessageDigest.getInstance("SHA-256").digest(
                text.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);
    }

}
