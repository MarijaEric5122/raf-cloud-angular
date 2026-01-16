package com.example.backend_MarijaNatasa.service;

import com.example.backend_MarijaNatasa.repository.UserRepository;
import com.example.backend_MarijaNatasa.user.User;
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

    // 1. GET ALL - Za tvoju tabelu na frontendu
    public List<User> findAll() {
        return userRepository.findAll();
    }

    // 2. GET ONE - Za edit formu
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    // 3. CREATE USER - Najbitnija metoda
    public User createUser(User user) {
        // PRAVILO: Email mora biti jedinstven
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Greška: Korisnik sa email-om " + user.getEmail() + " već postoji!");
        }

        // PRAVILO: Lozinka se mora čuvati u hash-ovanom obliku
        String hashedPass = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPass);

        return userRepository.save(user);
    }

    // 4. UPDATE USER
    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Korisnik nije pronađen pod ID-em: " + id));

        // Provera ako korisnik menja email na neki koji već postoji kod DRUGOG korisnika
        if (!user.getEmail().equals(userDetails.getEmail()) && userRepository.existsByEmail(userDetails.getEmail())) {
            throw new RuntimeException("Email je već zauzet!");
        }

        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setEmail(userDetails.getEmail());
        user.setPermissions(userDetails.getPermissions());

        // Lozinku obično ne menjamo kroz običan update profilnih podataka na ovaj način,
        // ali ako tvoj front šalje i lozinku, možeš dodati i nju (uz re-hashing).

        return userRepository.save(user);
    }

    // 5. DELETE USER
    public void deleteById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Korisnik ne postoji!");
        }
        userRepository.deleteById(id);
    }
}
