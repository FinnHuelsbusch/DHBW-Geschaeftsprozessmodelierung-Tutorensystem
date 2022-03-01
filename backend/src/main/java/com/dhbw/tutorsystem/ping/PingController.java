package com.dhbw.tutorsystem.ping;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
public class PingController {

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("Pong");
    }

    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @GetMapping("/ping/auth-student")
    public ResponseEntity<String> pingAuthStudent() {
        return ResponseEntity.ok("Pong Student");
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/ping/auth-admin")
    public ResponseEntity<String> pingAuthAdmin() {
        return ResponseEntity.ok("Pong Admin");
    }

    @PreAuthorize("hasRole('ROLE_DIRECTOR')")
    @GetMapping("/ping/auth-director")
    public ResponseEntity<String> pingAuthDirector() {
        return ResponseEntity.ok("Pong Director");
    }
}
