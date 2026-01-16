package com.example.backend_MarijaNatasa.repository;

import com.example.backend_MarijaNatasa.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    /*
     * Pronalazi korisnika na osnovu email adrese.
     * Ovo koristimo u AuthService-u prilikom logovanja.
     */
    Optional<User> findByEmail(String email);

    /*
     * Proverava da li u bazi već postoji korisnik sa zadatim email-om.
     * Ovo je obavezno jer po zahtevima email mora biti UNIQUE.
     * Koristićeš ovo u UserService-u pre nego što dozvoliš createUser.
     */
    boolean existsByEmail(String email);
}
