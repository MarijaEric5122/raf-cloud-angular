package com.example.backend_MarijaNatasa.filters;

import com.example.backend_MarijaNatasa.security.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.example.backend_MarijaNatasa.user.User;
import com.example.backend_MarijaNatasa.repository.UserRepository;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }
    private final UserRepository userRepository;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Preskačemo proveru ako je u pitanju login ruta
        String path = request.getRequestURI();
        if (path.equals("/auth/login")) {
            filterChain.doFilter(request, response);
            return;
        }
        if (path.startsWith("/ws")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Izvlačimo Authorization header
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Brišemo "Bearer " da ostane samo token

            try {
                if (jwtUtil.validateToken(token)) {
                    Claims claims = jwtUtil.extractClaims(token);

                    // 3. NAJBITNIJI DEO: Smeštamo email i permisije u request atribute
                    // Naš SecurityAspect (AOP) će ih odavde čitati
                    request.setAttribute("email", claims.getSubject());
                    request.setAttribute("permissions", claims.get("permissions", List.class));

                    User user = userRepository.findByEmail(claims.getSubject()).orElse(null);
                    if (user != null) {
                        request.setAttribute("user", user);
                    }
                }
            } catch (Exception e) {
                // Ako je token nevalidan, samo nastavljamo (AOP će kasnije baciti 403 jer nema permisija)
                System.out.println("Nevalidan token: " + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}
