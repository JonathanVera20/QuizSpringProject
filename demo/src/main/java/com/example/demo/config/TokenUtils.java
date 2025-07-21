package com.example.demo.config;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class TokenUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", userDetails.getUsername());

        // Add user authorities/roles to JWT for better security
        claims.put("authorities", userDetails.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .toList());

        // Add issued at claim for additional security
        claims.put("iat", new Date(System.currentTimeMillis()));

        return createToken(claims, userDetails.getUsername());
    }

    public String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, java.util.function.Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Boolean validateToken(String token) {
        try {
            final String username = getUsernameFromToken(token);
            System.out.println("Token validation - username: " + username);
            boolean isNotExpired = !isTokenExpired(token);
            System.out.println("Token validation - not expired: " + isNotExpired);
            boolean isValid = username != null && isNotExpired;
            System.out.println("Token validation - final result: " + isValid);
            return isValid;
        } catch (JwtException e) {
            System.out.println("Token validation error: " + e.getMessage());
            return false;
        }
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public UsernamePasswordAuthenticationToken getAuthentication(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String username = claims.getSubject();
            System.out.println("Authentication - extracted username: " + username);
            return new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
        } catch (JwtException e) {
            System.out.println("Authentication parsing error: " + e.getMessage());
            return null;
        }
    }
}
