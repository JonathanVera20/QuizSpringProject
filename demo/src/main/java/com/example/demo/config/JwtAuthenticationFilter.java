package com.example.demo.config;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final TokenUtils tokenUtils;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, TokenUtils tokenUtils) {
        this.authenticationManager = authenticationManager;
        this.tokenUtils = tokenUtils;
        setFilterProcessesUrl("/api/auth/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        AuthCredentials authCredentials;
        try {
            authCredentials = new ObjectMapper().readValue(request.getReader(), AuthCredentials.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse authentication request", e);
        }

        UsernamePasswordAuthenticationToken usernamePAT = new UsernamePasswordAuthenticationToken(
                authCredentials.getUsername(), authCredentials.getPassword(), Collections.emptyList());

        return authenticationManager.authenticate(usernamePAT);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        UserDetails userDetails = (UserDetails) authResult.getPrincipal();
        String token = tokenUtils.generateToken(userDetails);
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"token\":\"" + token + "\",\"type\":\"Bearer\"}");
        response.getWriter().flush();
    }
}