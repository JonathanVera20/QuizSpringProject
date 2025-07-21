package com.example.demo.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private UserDetailsService userDetailsService;

    // List of public endpoints that don't require authentication
    private final List<String> publicEndpoints = Arrays.asList(
            "/api/auth/login",
            "/api/auth/register",
            "/api/health",
            "/api/test",
            "/actuator/health",
            "/actuator/info"
    );

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        String method = request.getMethod();

        // Skip filter for OPTIONS requests
        if ("OPTIONS".equalsIgnoreCase(method)) {
            System.out.println("Skipping filter for OPTIONS request: " + path);
            return true;
        }

        // Only skip filter for exact public endpoints (not prefix match)
        boolean isPublic = publicEndpoints.stream().anyMatch(pub -> pub.equals(path));

        // Debug logging
        System.out.println("shouldNotFilter - Request URI: " + path + ", Method: " + method + ", Is Public: " + isPublic);

        if (isPublic) {
            System.out.println("FILTER WILL BE SKIPPED for public endpoint: " + path);
        } else {
            System.out.println("FILTER WILL BE APPLIED for protected endpoint: " + path);
        }

        return isPublic;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String bearerToken = request.getHeader("Authorization");
        System.out.println("Authorization header: " + bearerToken);

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.replace("Bearer ", "");
            System.out.println("Extracted token: " + token.substring(0, Math.min(token.length(), 20)) + "...");

            try {
                if (tokenUtils.validateToken(token)) {
                    String username = tokenUtils.getUsernameFromToken(token);
                    System.out.println("Token valid for user: " + username);

                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    System.out.println("User details loaded: " + userDetails.getUsername() + ", Authorities: " + userDetails.getAuthorities());

                    UsernamePasswordAuthenticationToken authentication
                            = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    System.out.println("Authentication set successfully");
                    System.out.println("SecurityContext authentication: " + SecurityContextHolder.getContext().getAuthentication());
                    System.out.println("Is authenticated: " + SecurityContextHolder.getContext().getAuthentication().isAuthenticated());
                } else {
                    System.out.println("Token validation failed");
                }
            } catch (RuntimeException e) {
                System.out.println("JWT Token validation failed: " + e.getMessage());
            }
        } else {
            System.out.println("No valid Bearer token found");
        }

        // Debug: Check authentication before passing to next filter
        System.out.println("Before filterChain - Authentication: " + SecurityContextHolder.getContext().getAuthentication());

        filterChain.doFilter(request, response);

        // Debug: Check authentication after filterChain
        System.out.println("After filterChain - Authentication: " + SecurityContextHolder.getContext().getAuthentication());
    }
}
