package com.example.backend.service;

import com.example.backend.dto.LoginRequest;
import com.example.backend.dto.LoginResponse;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.JwtUtil;
import com.example.backend.user.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public LoginResponse login(LoginRequest loginRequest) {
        Optional<User> optionalUser = userRepository.findByEmail(loginRequest.getEmail());

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {

                String token = jwtUtil.generateToken(user.getEmail(), new ArrayList<>(user.getPermissions()));

                return new LoginResponse(token, user.getEmail(), user.getPermissions());
            }
        }

        // invalid credentials
        throw new RuntimeException("Neispravan email ili lozinka!");
    }


}
