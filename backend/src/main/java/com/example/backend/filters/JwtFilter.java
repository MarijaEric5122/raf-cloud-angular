package com.example.backend.filters;

import com.example.backend.security.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.example.backend.user.User;
import com.example.backend.repository.UserRepository;

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

        // skip auth for login route
        String path = request.getRequestURI();
        if (path.equals("/auth/login")) {
            filterChain.doFilter(request, response);
            return;
        }
        if (path.startsWith("/ws")) {
            filterChain.doFilter(request, response);
            return;
        }

        // read Authorization header
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // remove "Bearer " prefix

            try {
                if (jwtUtil.validateToken(token)) {
                    Claims claims = jwtUtil.extractClaims(token);

                    // attach email and permissions to request
                    request.setAttribute("email", claims.getSubject());
                    request.setAttribute("permissions", claims.get("permissions", List.class));

                    User user = userRepository.findByEmail(claims.getSubject()).orElse(null);
                    if (user != null) {
                        request.setAttribute("user", user);
                    }
                }
            } catch (Exception e) {
                // ignore invalid token
                System.out.println("Nevalidan token: " + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}
