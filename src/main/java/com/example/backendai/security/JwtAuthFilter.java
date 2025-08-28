package com.example.backendai.security;

import com.example.backendai.model.User;
import com.example.backendai.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                logger.debug("JwtAuthFilter: found Authorization header for {} {}, token present", request.getMethod(), request.getRequestURI());
                if (jwtService.isTokenValid(token)) {
                    Long userId = jwtService.getUserId(token);
                    logger.debug("JwtAuthFilter: token valid, userId={}", userId);
                    if (userId != null) {
                        User user = userRepository.findById(userId).orElse(null);
                        if (user != null) {
                            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
                            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                            logger.debug("JwtAuthFilter: authentication set for user id={}", user.getId());
                        } else {
                            logger.debug("JwtAuthFilter: user not found for id={}", userId);
                        }
                    }
                } else {
                    logger.debug("JwtAuthFilter: token invalid");
                }
            } catch (Exception ex) {
                logger.debug("JwtAuthFilter: exception while processing token", ex);
                // ignore and continue
            }
        }
        filterChain.doFilter(request, response);
    }
}
