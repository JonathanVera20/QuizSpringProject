package com.example.demo.config;

import org.springframework.stereotype.Component;
import java.util.regex.Pattern;

@Component
public class SecurityUtils {

    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    // Username validation pattern (alphanumeric and underscore only)
    private static final Pattern USERNAME_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_]{3,20}$"
    );

    // Password strength validation
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
    );

    /**
     * Validates email format
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Validates username format
     */
    public static boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return USERNAME_PATTERN.matcher(username.trim()).matches();
    }

    /**
     * Validates password strength
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }
        // For production, you might want to enforce stronger passwords
        return password.length() >= 6;
    }

    /**
     * Sanitizes input string to prevent XSS
     */
    public static String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        
        return input
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#x27;")
            .replace("&", "&amp;");
    }

    /**
     * Validates and sanitizes username
     */
    public static String validateAndSanitizeUsername(String username) {
        if (!isValidUsername(username)) {
            throw new IllegalArgumentException("Invalid username format");
        }
        return sanitizeInput(username.trim());
    }

    /**
     * Validates and sanitizes email
     */
    public static String validateAndSanitizeEmail(String email) {
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format");
        }
        return email.trim().toLowerCase();
    }

    /**
     * Checks if password meets minimum requirements
     */
    public static boolean meetsPasswordRequirements(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }
        
        // Check for at least one letter and one number
        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        boolean hasNumber = password.matches(".*\\d.*");
        
        return hasLetter && hasNumber;
    }

    /**
     * Generates a secure random string for tokens
     */
    public static String generateSecureToken() {
        return java.util.UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * Validates JWT token format
     */
    public static boolean isValidJwtFormat(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        
        // Basic JWT format validation (3 parts separated by dots)
        String[] parts = token.split("\\.");
        return parts.length == 3;
    }
}