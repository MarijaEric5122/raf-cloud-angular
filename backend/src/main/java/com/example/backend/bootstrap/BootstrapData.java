package com.example.backend.bootstrap;

import com.example.backend.repository.UserRepository;
import com.example.backend.user.User;
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
        // full admin permissions
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

        // upsert admin user
        userRepository.findByEmail("admin@raf.rs").ifPresentOrElse(admin -> {
            admin.setPermissions(permissions);
            userRepository.save(admin);
            System.out.println("Admin pronađen – permisije osvežene.");
        }, () -> {
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
