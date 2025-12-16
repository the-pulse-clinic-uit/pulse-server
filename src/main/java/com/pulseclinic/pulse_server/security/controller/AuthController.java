package com.pulseclinic.pulse_server.security.controller;

import com.pulseclinic.pulse_server.security.dto.AuthResponse;
import com.pulseclinic.pulse_server.security.dto.LoginRequest;
import com.pulseclinic.pulse_server.security.dto.RegisterRequest;
import com.pulseclinic.pulse_server.security.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        AuthResponse authResponse = authService.register(registerRequest);
        if(authResponse != null) {
            return ResponseEntity.ok(authResponse);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse authResponse = authService.authenticate(loginRequest);
        if (authResponse != null) {
            return ResponseEntity.ok(authResponse);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
