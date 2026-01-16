package com.example.backend_MarijaNatasa.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckPermission {
    String value(); // Ovde ćemo prosleđivati npr. "CAN_READ_USERS"
}
