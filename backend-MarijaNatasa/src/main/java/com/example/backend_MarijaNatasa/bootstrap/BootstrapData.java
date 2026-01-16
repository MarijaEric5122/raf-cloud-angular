package com.example.backend_MarijaNatasa.bootstrap;

import com.example.backend_MarijaNatasa.repository.UserRepository;
import com.example.backend_MarijaNatasa.user.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class BootstrapData implements CommandLineRunner {
    private  UserRepository userRepository;
    private  PasswordEncoder passwordEncoder;

    public BootstrapData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // 1. Definišemo SVE permisije koje admin treba da ima
        Set<String> permissions = new HashSet<>();
        permissions.add("CAN_READ_USERS");
        permissions.add("CAN_CREATE_USERS");
        permissions.add("CAN_UPDATE_USERS");
        permissions.add("CAN_DELETE_USERS");

        permissions.add("CAN_SEARCH_MACHINES");
        permissions.add("CAN_START_MACHINES");
        permissions.add("CAN_STOP_MACHINES");
        permissions.add("CAN_RESTART_MACHINES");
        permissions.add("CAN_CREATE_MACHINES");
        permissions.add("CAN_DESTROY_MACHINES");

        permissions.add("CAN_READ_SCHEDULES");
        permissions.add("CAN_READ_ERRORS");
        permissions.add("is_admin");

        // 2. Ako admin postoji -> UPDATE permisija
        //    Ako ne postoji -> CREATE admin korisnika
        userRepository.findByEmail("admin@raf.rs").ifPresentOrElse(admin -> {
            // Već postoji u bazi – samo mu setujemo sve permisije iz početka
            admin.setPermissions(permissions);
            userRepository.save(admin);
            System.out.println("Admin pronađen – permisije osvežene.");
        }, () -> {
            // Ne postoji – pravimo novog admina
            User admin = new User();
            admin.setFirstName("Admin");
            admin.setLastName("Adminovic");
            admin.setEmail("admin@raf.rs");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setPermissions(permissions);
            userRepository.save(admin);
            System.out.println("Sistem inicijalizovan sa admin korisnikom.");
        });
    }


}
