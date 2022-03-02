package com.dhbw.tutorsystem.security.authentication;

import com.dhbw.tutorsystem.exception.TSExceptionResponse;
import com.dhbw.tutorsystem.exception.TSServerError;
import com.dhbw.tutorsystem.mails.EmailSenderService;
import com.dhbw.tutorsystem.mails.MailType;
import com.dhbw.tutorsystem.role.ERole;
import com.dhbw.tutorsystem.role.Role;
import com.dhbw.tutorsystem.role.RoleRepository;
import com.dhbw.tutorsystem.security.authentication.exception.InvalidHashException;
import com.dhbw.tutorsystem.security.authentication.exception.LastPasswordActionTooRecentException;
import com.dhbw.tutorsystem.security.authentication.exception.MailSendException;
import com.dhbw.tutorsystem.security.authentication.exception.ResetPasswordAccountNotEnabledException;
import com.dhbw.tutorsystem.security.authentication.exception.RoleNotFoundException;
import com.dhbw.tutorsystem.security.authentication.exception.UserAlreadyEnabledException;
import com.dhbw.tutorsystem.security.authentication.exception.EmailAlreadyExistsException;
import com.dhbw.tutorsystem.security.authentication.exception.HashGenerationException;
import com.dhbw.tutorsystem.security.authentication.exception.UserNotFoundException;
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
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
// import io.swagger.v3.oas.annotations.responses.ApiResponses;
// import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

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
import java.util.Map;
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
            @ApiResponse(responseCode = "200", description = "Login was successful. User will be logged in using the token in the response."),
            @ApiResponse(responseCode = "401", description = "Login was not successful.")
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
            @ApiResponse(responseCode = "200", description = "Registration email was sent successfully."),
            @ApiResponse(responseCode = "400", description = "Error in processing defined by error message and error code in response.", content = @Content(schema = @Schema(implementation = TSExceptionResponse.class)))
    })
    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest registerRequest) {
        // check for duplicate registration, then send mail and update or save user
        Optional<User> optionalUser = userRepository.findByEmail(registerRequest.getEmail());
        if (optionalUser.isPresent()) {
            // same email address as an existing user was provided for registration
            User user = optionalUser.get();
            if (user.isEnabled()) {
                throw new EmailAlreadyExistsException();
            }
            if (Duration.between(user.getLastPasswordAction(), LocalDateTime.now()).toMinutes() < 15) {
                throw new LastPasswordActionTooRecentException();
            } else {
                // existing non-enabled user re-registered after 15minutes: re-send email and
                // update last changed
                user.setLastPasswordAction(LocalDateTime.now());
                try {
                    sendRegisterMail(user.getEmail(), user.getLastPasswordAction());
                } catch (HashGenerationException | MailSendException e) {
                    throw e;
                }
                user = userRepository.save(user);
            }
        } else {
            // new email address was provided for registration: encode password and save new
            // user
            String encodedPassword = encoder.encode(registerRequest.getPassword());
            User user = new User(registerRequest.getEmail(), encodedPassword);

            Optional<Role> role = Optional.empty();
            if (user.isStudentMail()) {
                role = roleRepository.findByName(ERole.ROLE_STUDENT);
            } else if (user.isDirectorMail()) {
                role = roleRepository.findByName(ERole.ROLE_DIRECTOR);
            }
            if (role.isEmpty()) {
                throw new RoleNotFoundException();
            }
            user.setRoles(Set.of(role.get()));
            user.setEnabled(false);
            user.setLastPasswordAction(LocalDateTime.now());
            try {
                sendRegisterMail(user.getEmail(), user.getLastPasswordAction());
            } catch (HashGenerationException | MailSendException e) {
                throw e;
            }
            user = userRepository.save(user);
        }
        return ResponseEntity.ok(null);
    }

    @Operation(summary = "Enable an account using a hash value from an activation link after registration.", tags = {
            "authentication" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account was activated successfully. User will be directly logged in using token in response."),
            @ApiResponse(responseCode = "400", description = "Error in processing defined by error message and error code in response.", content = @Content(schema = @Schema(implementation = TSExceptionResponse.class)))
    })
    @PostMapping("/enableAccount")
    public ResponseEntity<?> enableUserAccount(@Valid @RequestBody VerifyRequest verifyRequest) {
        // find user by email and enable if hash is valid
        Optional<User> optionalUser = userRepository.findByEmail(verifyRequest.getEmail());
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException();
        }
        if (optionalUser.get().isEnabled()) {
            throw new UserAlreadyEnabledException();
        }
        User user = optionalUser.get();
        if (isHashClaimValid(verifyRequest.getHash(), user.getEmail(), user.getLastPasswordAction())) {
            user.setEnabled(true);
            user.setLastPasswordAction(LocalDateTime.now());
            user = userRepository.save(user);
            String jwt = jwtUtils.generateJwtTokenFromUsername(user.getEmail());
            return ResponseEntity.ok(new JwtResponse(Role.getRolesString(user.getRoles()), jwt,
                    jwtUtils.getExpirationDateFromJwtToken(jwt),
                    user.getEmail()));
        } else {
            throw new InvalidHashException();
        }
    }

    @Operation(summary = "Request to reset the password corresponding to a user account by email. Sends an email containing a link to reset the password.", tags = {
            "authentication" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset email was sent successfully."),
            @ApiResponse(responseCode = "400", description = "Error in processing defined by error message and error code in response.", content = @Content(schema = @Schema(implementation = TSExceptionResponse.class)))
    })
    @PostMapping("/requestPasswordReset")
    public ResponseEntity<?> resetPasswordRequest(
            @Valid @RequestBody RequestPasswordResetRequest requestResetPasswordRequest) {
        // find user and send hash via email for password reset
        Optional<User> optionalUser = userRepository.findByEmail(requestResetPasswordRequest.getEmail());
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException();
        }
        User user = optionalUser.get();
        if (!user.isEnabled()) {
            throw new ResetPasswordAccountNotEnabledException();
        }
        if (Duration.between(user.getLastPasswordAction(), LocalDateTime.now()).toMinutes() < 15) {
            throw new LastPasswordActionTooRecentException();
        }
        try {
            sendResetPasswordMail(user.getEmail(), user.getLastPasswordAction());
            user.setLastPasswordAction(LocalDateTime.now());
            userRepository.save(user);
        } catch (HashGenerationException | MailSendException e) {
            throw e;
        }
        return ResponseEntity.ok(null);
    }

    @Operation(summary = "Reset the password using a hash value from a link received after a password reset request.", tags = {
            "authentication" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password was reset successfully. User will be logged in directly using token in response."),
            @ApiResponse(responseCode = "400", description = "Error in processing defined by error message and error code in response.", content = @Content(schema = @Schema(implementation = TSExceptionResponse.class)))
    })
    @PostMapping("/resetPassword")
    public ResponseEntity<JwtResponse> resetUserPassword(
            @Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        Optional<User> optionalUser = userRepository.findByEmail(resetPasswordRequest.getEmail());
        if (optionalUser.isEmpty() || optionalUser.get().isEnabled()) {
            throw new UserNotFoundException();
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
            throw new InvalidHashException();
        }
    }

    private void sendRegisterMail(String userMail, LocalDateTime lastPasswordAction)
            throws HashGenerationException, MailSendException {
        try {
            String hashBase64 = createBase64VerificationHash(userMail, lastPasswordAction);
            emailSenderService.sendMail(userMail, MailType.REGISTRATION, Map.of("hashBase64", hashBase64));
        } catch (NoSuchAlgorithmException | MessagingException e) {
            e.printStackTrace();
            if (e instanceof NoSuchAlgorithmException) {
                throw new HashGenerationException();
            } else if (e instanceof MessagingException) {
                throw new MailSendException();
            }
        }
    }

    private void sendResetPasswordMail(String userMail, LocalDateTime lastPasswordAction)
            throws HashGenerationException, MailSendException {
        try {
            String hashBase64 = createBase64VerificationHash(userMail, lastPasswordAction);
            emailSenderService.sendMail(userMail, MailType.RESET_PASSWORD, Map.of("hashBase64", hashBase64));
        } catch (NoSuchAlgorithmException | MessagingException e) {
            e.printStackTrace();
            if (e instanceof NoSuchAlgorithmException) {
                throw new HashGenerationException();
            } else if (e instanceof MessagingException) {
                throw new MailSendException();
            }
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

    private String createBase64VerificationHash(String email, LocalDateTime timestamp) throws NoSuchAlgorithmException {
        String text = email + timestamp;
        byte[] hash = MessageDigest.getInstance("SHA-256").digest(
                text.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);
    }

}
