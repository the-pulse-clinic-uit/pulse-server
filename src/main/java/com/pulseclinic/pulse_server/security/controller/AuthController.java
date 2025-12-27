package com.pulseclinic.pulse_server.security.controller;

import com.pulseclinic.pulse_server.security.dto.*;
import com.pulseclinic.pulse_server.security.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
        if (authResponse != null) {
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

    @PostMapping("/forgot-password")
    public ResponseEntity<HttpStatus> forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        if (authService.generateResetToken(forgotPasswordRequest)) {
            return ResponseEntity.ok(HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/reset-password")
    // @PreAuthorize(isAuthenticated())
    // @PreAuthorize("hasAnyRole('DOCTOR','doctor')")
    public ResponseEntity<HttpStatus> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        if (authService.resetPassword(resetPasswordRequest)) {
            return ResponseEntity.ok(HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/logout")
    public ResponseEntity<HttpStatus> logout(Authentication authentication,
            @RequestHeader("Authorization") String authHeader) {
        String email = authentication.getName();
        String token = authHeader.substring(7);
        if (authService.logout(email, token))
            return ResponseEntity.ok().build();
        else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
