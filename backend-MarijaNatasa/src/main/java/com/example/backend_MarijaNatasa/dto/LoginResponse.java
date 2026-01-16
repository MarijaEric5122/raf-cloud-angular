package com.example.backend_MarijaNatasa.dto;

import lombok.Data;

import java.util.Set;
@Data
public class LoginResponse {
    private String jwt;
    private String email;
    private Set<String> permissions;

    public LoginResponse(String jwt, String email, Set<String> permissions) {
        this.jwt = jwt;
        this.email = email;
        this.permissions = permissions;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }
}
