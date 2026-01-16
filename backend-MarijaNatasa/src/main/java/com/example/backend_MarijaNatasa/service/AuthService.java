package com.example.backend_MarijaNatasa.service;

import com.example.backend_MarijaNatasa.dto.LoginRequest;
import com.example.backend_MarijaNatasa.dto.LoginResponse;
import com.example.backend_MarijaNatasa.repository.UserRepository;
import com.example.backend_MarijaNatasa.security.JwtUtil;
import com.example.backend_MarijaNatasa.user.User;
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
        // 1. Tražimo korisnika po emailu
        Optional<User> optionalUser = userRepository.findByEmail(loginRequest.getEmail());

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            // 2. Provera lozinke (raw password vs hashed password)
            if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {

                // 3. Generisanje tokena (prosleđujemo email i listu permisija)
                String token = jwtUtil.generateToken(user.getEmail(), new ArrayList<>(user.getPermissions()));

                // 4. Vraćamo odgovor koji Angular očekuje
                return new LoginResponse(token, user.getEmail(), user.getPermissions());
            }
        }

        // Ako podaci nisu tačni, bacamo grešku (možeš napraviti i custom exception)
        throw new RuntimeException("Neispravan email ili lozinka!");
    }


}
