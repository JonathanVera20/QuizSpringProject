package com.example.demo.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest
class TokenUtilsTest {

    private TokenUtils tokenUtils;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        tokenUtils = new TokenUtils();
        // Set the JWT secret and expiration for testing - using a proper 256-bit key
        ReflectionTestUtils.setField(tokenUtils, "jwtSecret", "MyVeryLongAndSecureJWTSecretKeyForHMACSHA256Algorithm123456789");
        ReflectionTestUtils.setField(tokenUtils, "jwtExpiration", 86400000L); // 24 hours

        // Mock UserDetails
        userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());
    }

    @Test
    void testGenerateToken() {
        String token = tokenUtils.generateToken(userDetails);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        System.out.println("Generated token: " + token.substring(0, Math.min(token.length(), 50)) + "...");
    }

    @Test
    void testValidateToken() {
        String token = tokenUtils.generateToken(userDetails);

        Boolean isValid = tokenUtils.validateToken(token);

        assertTrue(isValid);
        System.out.println("Token validation: " + isValid);
    }

    @Test
    void testGetUsernameFromToken() {
        String token = tokenUtils.generateToken(userDetails);

        String username = tokenUtils.getUsernameFromToken(token);

        assertEquals("testuser", username);
        System.out.println("Extracted username: " + username);
    }

    @Test
    void testGetAuthentication() {
        String token = tokenUtils.generateToken(userDetails);

        UsernamePasswordAuthenticationToken authentication = tokenUtils.getAuthentication(token);

        assertNotNull(authentication);
        assertEquals("testuser", authentication.getPrincipal());
        System.out.println("Authentication principal: " + authentication.getPrincipal());
    }

    @Test
    void testInvalidToken() {
        String invalidToken = "invalid.token.here";

        Boolean isValid = tokenUtils.validateToken(invalidToken);
        UsernamePasswordAuthenticationToken authentication = tokenUtils.getAuthentication(invalidToken);

        assertFalse(isValid);
        assertNull(authentication);
        System.out.println("Invalid token validation: " + isValid);
    }
}
