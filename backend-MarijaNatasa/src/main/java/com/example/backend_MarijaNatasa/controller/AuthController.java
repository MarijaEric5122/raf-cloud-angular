package com.example.backend_MarijaNatasa.controller;

import com.example.backend_MarijaNatasa.dto.LoginRequest;
import com.example.backend_MarijaNatasa.dto.LoginResponse;
import com.example.backend_MarijaNatasa.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin // Bitno da Angular može da pristupi (CORS)
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse response = authService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }
}
