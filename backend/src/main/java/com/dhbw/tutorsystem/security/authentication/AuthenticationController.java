package com.dhbw.tutorsystem.security.authentication;

import com.dhbw.tutorsystem.exception.TSServerError;
import com.dhbw.tutorsystem.mails.EmailSenderService;
import com.dhbw.tutorsystem.role.ERole;
import com.dhbw.tutorsystem.role.Role;
import com.dhbw.tutorsystem.role.RoleRepository;
import com.dhbw.tutorsystem.security.authentication.exception.InvalidEmailException;
import com.dhbw.tutorsystem.security.authentication.exception.MailSendException;
import com.dhbw.tutorsystem.security.authentication.exception.RegistrationMailAlreadySentException;
import com.dhbw.tutorsystem.security.authentication.exception.RoleNotFoundException;
import com.dhbw.tutorsystem.security.authentication.payload.JwtResponse;
import com.dhbw.tutorsystem.security.authentication.payload.LoginRequest;
import com.dhbw.tutorsystem.security.authentication.payload.RegisterRequest;
import com.dhbw.tutorsystem.security.authentication.payload.RequestPasswordResetRequest;
import com.dhbw.tutorsystem.security.authentication.payload.ResetPasswordRequest;
import com.dhbw.tutorsystem.security.authentication.payload.VerifyRequest;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import org.springframework.security.crypto.password.PasswordEncoder;

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

    @Operation(summary = "Login a user based on email and password.", tags = { "authentication" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login was successful"),
            @ApiResponse(responseCode = "401", description = "Login was not successful")
    })
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        String jwt = jwtUtils.generateJwtTokenFromAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(roles, jwt, jwtUtils.getExpirationDateFromJwtToken(jwt),
                userDetails.getEmailAddress()));
    }

    @Operation(summary = "Create a not-enabled account for a user using email and password and send registration email with activation link.", tags = {
            "authentication" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registration email was sent successfully"),
            @ApiResponse(responseCode = "400", description = "User already exists or existing registration request is pending since less than 15 minutes")
    })
    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest registerRequest) {
        Optional<User> optionalUser = userRepository.findByEmail(registerRequest.getEmail());
        User user;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
            if (user.isEnabled()) {
                throw new InvalidEmailException();
            }
            if (Duration.between(user.getLastPasswordAction(), LocalDateTime.now()).toMinutes() < 15) {
                throw new RegistrationMailAlreadySentException();
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
                throw new InvalidEmailException();
            }
            if (role.isEmpty()) {
                throw new RoleNotFoundException();
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
            throw new TSServerError();
        }
        try {
            emailSenderService.sendRegistrationMail(user.getEmail(), hashBase64);
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new MailSendException();
        }
        userRepository.save(user);
        return ResponseEntity.ok(null);
    }

    @Operation(summary = "Enable an account using a hash value from an activation link after registration.", tags = {
            "authentication" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account was activated successfully"),
            @ApiResponse(responseCode = "400", description = "Account was not activated because the supplied hash claim is not valid")
    })
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
            String jwt = jwtUtils.generateJwtTokenFromUsername(user.getEmail());
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

    @Operation(summary = "Request to reset the password corresponding to a user account by email. Sends an email containing a link to reset the password.", tags = {
            "authentication" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset email was sent successfully"),
            @ApiResponse(responseCode = "400", description = "User does not exist or has recently reset the password or the email could not be sent")
    })
    @PostMapping("/requestPasswordReset")
    public ResponseEntity<String> resetPasswordRequest(
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

    @Operation(summary = "Reset the password using a hash value from a link received after a password reset request.", tags = {
            "authentication" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password was reset successfully"),
            @ApiResponse(responseCode = "400", description = "Password could not be reset because hash claim was not valid")
    })
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
            String jwt = jwtUtils.generateJwtTokenFromUsername(user.getEmail());
            return ResponseEntity.ok(new JwtResponse(Role.getRolesString(user.getRoles()), jwt,
                    jwtUtils.getExpirationDateFromJwtToken(jwt),
                    user.getEmail()));
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
