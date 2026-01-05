package com.pulseclinic.pulse_server.security;

import com.pulseclinic.pulse_server.security.dto.*;
import com.pulseclinic.pulse_server.security.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            AuthResponse authResponse = authService.register(registerRequest);
            if (authResponse == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            AuthResponse authResponse = authService.authenticate(loginRequest);
            if (authResponse == null) {
                throw new Exception("Invalid username or password");
            }
            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<HttpStatus> forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        try {
            if (!authService.generateResetToken(forgotPasswordRequest)) {
                throw new RuntimeException("Email not exists");
            }
            return ResponseEntity.ok(HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/reset-password")
    // @PreAuthorize(isAuthenticated())
    // @PreAuthorize("hasAnyRole('DOCTOR','doctor')")
    public ResponseEntity<HttpStatus> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        try {
            if (!authService.resetPassword(resetPasswordRequest)) {
                throw new RuntimeException("Bad Request");
            }
            return ResponseEntity.ok(HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<HttpStatus> logout(
            Authentication authentication,
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            String email = authentication.getName();
            String token = authHeader.substring(7);
            if (!authService.logout(email, token))
                throw new RuntimeException("Unauthorized");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
