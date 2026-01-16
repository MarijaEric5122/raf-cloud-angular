package com.example.backend.service;

import com.example.backend.repository.UserRepository;
import com.example.backend.user.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User createUser(User user) {
        // email must be unique
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Greška: Korisnik sa email-om " + user.getEmail() + " već postoji!");
        }

        // hash password before save
        String hashedPass = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPass);

        return userRepository.save(user);
    }

    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Korisnik nije pronađen pod ID-em: " + id));

        // prevent duplicate email
        if (!user.getEmail().equals(userDetails.getEmail()) && userRepository.existsByEmail(userDetails.getEmail())) {
            throw new RuntimeException("Email je već zauzet!");
        }

        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setEmail(userDetails.getEmail());
        user.setPermissions(userDetails.getPermissions());

        return userRepository.save(user);
    }

    public void deleteById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Korisnik ne postoji!");
        }
        userRepository.deleteById(id);
    }
}
