package com.example.backend_MarijaNatasa.controller;

import com.example.backend_MarijaNatasa.security.CheckPermission;
import com.example.backend_MarijaNatasa.service.UserService;
import com.example.backend_MarijaNatasa.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin // Omogućava Angularu da pristupi rutama
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 1. LISTANJE SVIH KORISNIKA
    @CheckPermission("CAN_READ_USERS")
    @GetMapping
    public List<User> getAllUsers() {
        return userService.findAll();
    }

    // 2. DOBAVLJANJE JEDNOG KORISNIKA (za Edit formu)
    @CheckPermission("CAN_READ_USERS")
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 3. KREIRANJE NOVOG KORISNIKA
    @CheckPermission("CAN_CREATE_USERS")
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (RuntimeException e) {
            // Ako email već postoji, vraćamo grešku (409 Conflict ili 400 Bad Request)
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    // 4. AŽURIRANJE KORISNIKA
    @CheckPermission("CAN_UPDATE_USERS")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            User updatedUser = userService.updateUser(id, user);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 5. BRISANJE KORISNIKA
    @CheckPermission("CAN_DELETE_USERS")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteById(id);
            return ResponseEntity.ok().build(); // Vraćamo 200 OK ako je brisanje uspešno
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
