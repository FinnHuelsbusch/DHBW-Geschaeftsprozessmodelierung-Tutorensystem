package com.dhbw.tutorsystem.security.authentication;

import com.dhbw.tutorsystem.exception.TSExceptionResponse;
import com.dhbw.tutorsystem.exception.TSInternalServerException;
import com.dhbw.tutorsystem.mails.EmailSenderService;
import com.dhbw.tutorsystem.mails.MailType;
import com.dhbw.tutorsystem.role.ERole;
import com.dhbw.tutorsystem.role.Role;
import com.dhbw.tutorsystem.role.RoleRepository;
import com.dhbw.tutorsystem.security.authentication.exception.LastPasswordActionTooRecentException;
import com.dhbw.tutorsystem.security.authentication.exception.LoginFailedException;
import com.dhbw.tutorsystem.security.authentication.exception.AccountNotEnabledException;
import com.dhbw.tutorsystem.security.authentication.exception.RoleNotFoundException;
import com.dhbw.tutorsystem.security.authentication.exception.UserAlreadyEnabledException;
import com.dhbw.tutorsystem.security.authentication.exception.EmailAlreadyExistsException;
import com.dhbw.tutorsystem.security.authentication.exception.InvalidEmailException;
import com.dhbw.tutorsystem.security.authentication.exception.UserNotFoundException;
import com.dhbw.tutorsystem.security.authentication.payload.JwtResponse;
import com.dhbw.tutorsystem.security.authentication.payload.LoginRequest;
import com.dhbw.tutorsystem.security.authentication.payload.RegisterRequest;
import com.dhbw.tutorsystem.security.authentication.payload.RequestPasswordResetRequest;
import com.dhbw.tutorsystem.security.authentication.payload.ChangePasswordRequest;
import com.dhbw.tutorsystem.security.authentication.payload.ResetPasswordRequest;
import com.dhbw.tutorsystem.security.authentication.payload.VerifyRequest;
import com.dhbw.tutorsystem.security.jwt.JwtUtils;
import com.dhbw.tutorsystem.security.services.UserDetailsImpl;
import com.dhbw.tutorsystem.user.User;
import com.dhbw.tutorsystem.user.UserRepository;
import com.dhbw.tutorsystem.user.UserService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
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
import java.util.stream.Stream;

@RestController
@RequestMapping("/authentication")
public class AuthenticationController {

    @Value("${backend.app.minutesBetweenPasswordActions}")
    private int minimumMinutesBetweenPasswordActions;

    final AuthenticationManager authenticationManager;
    final UserRepository userRepository;
    final RoleRepository roleRepository;
    final JwtUtils jwtUtils;
    final PasswordEncoder encoder;
    final EmailSenderService emailSenderService;
    final UserService userService;

    public AuthenticationController(AuthenticationManager authenticationManager, UserRepository userRepository,
            RoleRepository roleRepository, JwtUtils jwtUtils, PasswordEncoder encoder,
            EmailSenderService emailSenderService, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.jwtUtils = jwtUtils;
        this.encoder = encoder;
        this.emailSenderService = emailSenderService;
        this.userService = userService;
    }

    @Operation(summary = "Login a user based on email and password.", tags = { "authentication" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login was successful. User is logged by using the token in the response."),
            @ApiResponse(responseCode = "400", description = "Login was not successful.", content = @Content(schema = @Schema(implementation = TSExceptionResponse.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        if (!User.isValidEmail(loginRequest.getEmail())) {
            throw new LoginFailedException();
        }
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
            UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
            String jwt = jwtUtils.generateJwtTokenFromUsername(userPrincipal.getEmailAddress());

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new JwtResponse(roles, jwt, jwtUtils.getExpirationDateFromJwtToken(jwt),
                    userDetails.getEmailAddress()));
        } catch (AuthenticationException e) {
            throw new LoginFailedException();
        }
    }

    @Operation(summary = "Create user.", description = "Create a not-enabled account for a user using email and password and send registration email with activation link.", tags = {
            "authentication" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registration email with activation link was sent successfully."),
            @ApiResponse(responseCode = "400", description = "Error in processing defined by error message and error code in response.", content = @Content(schema = @Schema(implementation = TSExceptionResponse.class)))
    })
    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest registerRequest) {
        if (!User.isValidEmail(registerRequest.getEmail())) {
            throw new InvalidEmailException();
        }
        // check for duplicate registration, then send mail and update or save user
        Optional<User> optionalUser = userRepository.findByEmail(registerRequest.getEmail());
        if (optionalUser.isPresent()) {
            // same email address as an existing user was provided for registration
            User user = optionalUser.get();
            if (user.isEnabled()) {
                throw new EmailAlreadyExistsException();
            }
            if (Duration.between(user.getLastPasswordAction(), LocalDateTime.now())
                    .toMinutes() < minimumMinutesBetweenPasswordActions) {
                throw new LastPasswordActionTooRecentException();
            } else {
                // existing non-enabled user re-registered after 15minutes: re-send email and
                // update last changed
                user.setLastPasswordAction(LocalDateTime.now());
                try {
                    sendRegisterMail(user.getEmail(), user.getLastPasswordAction(), false);
                } catch (NoSuchAlgorithmException | MessagingException e) {
                    throw new TSInternalServerException();
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
            // role might not be available in DB
            if (role.isEmpty()) {
                throw new RoleNotFoundException();
            } else {
                user.setRoles(Set.of(role.get()));
            }
            user.setEnabled(false);
            user.setLastPasswordAction(LocalDateTime.now());
            try {
                sendRegisterMail(user.getEmail(), user.getLastPasswordAction(), true);
            } catch (NoSuchAlgorithmException | MessagingException e) {
                throw new TSInternalServerException();
            }
            user = userRepository.save(user);
        }
        return ResponseEntity.ok(null);
    }

    @Operation(summary = "Enable user.", description = "Enable an account using a hash value from an activation link after registration.", tags = {
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
        if (isHashClaimValid(verifyRequest.getHash(), user.getEmail(), user.getLastPasswordAction().toString())) {
            user.setEnabled(true);
            user.setLastPasswordAction(LocalDateTime.now());
            user = userRepository.save(user);
            String jwt = jwtUtils.generateJwtTokenFromUsername(user.getEmail());
            return ResponseEntity.ok(new JwtResponse(Role.getRolesString(user.getRoles()), jwt,
                    jwtUtils.getExpirationDateFromJwtToken(jwt),
                    user.getEmail()));
        } else {
            throw new TSInternalServerException();
        }
    }

    @Operation(summary = "Request a password reset for forgotten password.", description = "Request to reset the password corresponding to a user account by email. Sends an email containing a link to reset the password.", tags = {
            "authentication" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset email was sent successfully."),
            @ApiResponse(responseCode = "400", description = "Error in processing defined by error message and error code in response.", content = @Content(schema = @Schema(implementation = TSExceptionResponse.class)))
    })
    @PostMapping("/requestPasswordReset")
    public ResponseEntity<?> resetPasswordRequest(
            @Valid @RequestBody RequestPasswordResetRequest requestPasswordResetRequest) {
        // find user, create hash of email/timestamp/newPassword and send it to the
        // email address
        Optional<User> optionalUser = userRepository.findByEmail(requestPasswordResetRequest.getEmail());
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException();
        }
        User user = optionalUser.get();
        if (!user.isEnabled()) {
            throw new AccountNotEnabledException();
        }
        if (Duration.between(user.getLastPasswordAction(), LocalDateTime.now())
                .toMinutes() < minimumMinutesBetweenPasswordActions) {
            throw new LastPasswordActionTooRecentException();
        }
        try {
            LocalDateTime newLastPasswordAction = LocalDateTime.now();
            sendResetPasswordMail(user.getEmail(), newLastPasswordAction,
                    requestPasswordResetRequest.getNewPassword());
            user.setLastPasswordAction(newLastPasswordAction);
            user.setTempPassword(encoder.encode(requestPasswordResetRequest.getNewPassword()));
            userRepository.save(user);
            return ResponseEntity.ok(null);
        } catch (NoSuchAlgorithmException | MessagingException e) {
            throw new TSInternalServerException();
        }
    }

    @Operation(summary = "Reset a password that was forgotten.", description = "Reset the password using a hash value and the new password from a link received after a password reset request.", tags = {
            "authentication" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password was reset successfully. User will be logged in directly using token in response."),
            @ApiResponse(responseCode = "400", description = "Error in processing defined by error message and error code in response.", content = @Content(schema = @Schema(implementation = TSExceptionResponse.class)))
    })
    @PostMapping("/performPasswordReset")
    public ResponseEntity<JwtResponse> resetUserPassword(
            @Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        Optional<User> optionalUser = userRepository.findByEmail(resetPasswordRequest.getEmail());
        // not-enabled users should follow the register email link first, so throw
        // exception
        if (optionalUser.isEmpty()) {
            throw new TSInternalServerException();
        }
        User user = optionalUser.get();
        if (!user.isEnabled() || user.getTempPassword() == null) {
            throw new TSInternalServerException();
        }
        if (isHashClaimValid(resetPasswordRequest.getHash(), user.getEmail(),
                user.getLastPasswordAction().toString(), resetPasswordRequest.getNewPassword())) {
            // set new encoded password, update last action, delete temp password and
            // directly login user
            user.setPassword(encoder.encode(resetPasswordRequest.getNewPassword()));
            user.setLastPasswordAction(LocalDateTime.now());
            user.setTempPassword(null);
            user = userRepository.save(user);
            String jwt = jwtUtils.generateJwtTokenFromUsername(user.getEmail());
            return ResponseEntity.ok(new JwtResponse(Role.getRolesString(user.getRoles()), jwt,
                    jwtUtils.getExpirationDateFromJwtToken(jwt),
                    user.getEmail()));
        } else {
            throw new TSInternalServerException();
        }
    }

    @Operation(summary = "Change a password while being logged in.", description = "Change the password into a new password when being logged in. Only Students and Directors can reset their passwords.", tags = {
            "authentication" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password was changed successfully. The user will be directly logged in using the token in the response."),
            @ApiResponse(responseCode = "400", description = "Error in processing defined by error message and error code in response.", content = @Content(schema = @Schema(implementation = TSExceptionResponse.class)))
    })
    @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_DIRECTOR')")
    @PostMapping("/changePassword")
    public ResponseEntity<JwtResponse> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        // find user and change the password, then directly log in
        User loggedUser = userService.getLoggedInUser();
        if (loggedUser == null) {
            throw new UserNotFoundException();
        }
        Optional<User> optionalUser = userRepository.findByEmail(loggedUser.getEmail());
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException();
        }
        User user = optionalUser.get();
        if (!user.isEnabled()) {
            throw new AccountNotEnabledException();
        }
        if (Duration.between(user.getLastPasswordAction(),
                LocalDateTime.now()).toMinutes() < minimumMinutesBetweenPasswordActions) {
            throw new LastPasswordActionTooRecentException();
        }
        // set new password and update user
        user.setPassword(encoder.encode(changePasswordRequest.getNewPassword()));
        user.setLastPasswordAction(LocalDateTime.now());
        user = userRepository.save(user);
        String jwt = jwtUtils.generateJwtTokenFromUsername(user.getEmail());
        return ResponseEntity.ok(new JwtResponse(Role.getRolesString(user.getRoles()), jwt,
                jwtUtils.getExpirationDateFromJwtToken(jwt),
                user.getEmail()));
    }

    private void sendRegisterMail(String userMail, LocalDateTime lastPasswordAction, boolean isFirstRegisterMail)
            throws NoSuchAlgorithmException, MessagingException {
        try {
            String hashBase64 = createBase64VerificationHash(userMail, lastPasswordAction.toString());
            emailSenderService.sendMail(userMail, MailType.REGISTRATION, Map.of(
                    "hashBase64", hashBase64,
                    "isFirstRegisterMail", isFirstRegisterMail));
        } catch (NoSuchAlgorithmException | MessagingException e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void sendResetPasswordMail(String userMail, LocalDateTime lastPasswordAction, String newPassword)
            throws NoSuchAlgorithmException, MessagingException {
        try {
            String hashBase64 = createBase64VerificationHash(userMail, lastPasswordAction.toString(), newPassword);
            emailSenderService.sendMail(userMail, MailType.RESET_PASSWORD, Map.of(
                    "hashBase64", hashBase64));
        } catch (NoSuchAlgorithmException | MessagingException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public boolean isHashClaimValid(String hashClaim, String... rawComponents) {
        try {
            String hashBase64Expected = createBase64VerificationHash(rawComponents);
            return StringUtils.equals(hashBase64Expected, hashClaim);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String createBase64VerificationHash(String... rawComponents) throws NoSuchAlgorithmException {
        String rawText = Stream.of(rawComponents).reduce("", (s1, s2) -> s1 + s2);
        byte[] hash = MessageDigest.getInstance("SHA-256").digest(
                rawText.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);
    }

}
