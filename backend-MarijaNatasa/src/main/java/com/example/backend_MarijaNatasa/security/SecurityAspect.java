package com.example.backend_MarijaNatasa.security;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

@Aspect
@Component
public class SecurityAspect {
    @Before("@annotation(checkPermission)")
    public void check(CheckPermission checkPermission) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        // Uzimamo permisije koje je naš Filter stavio u request atribute
        List<String> userPermissions = (List<String>) request.getAttribute("permissions");

        if (userPermissions == null || !userPermissions.contains(checkPermission.value())) {
            throw new RuntimeException("403 Forbidden: Nemate dozvolu " + checkPermission.value());
        }
    }
}
