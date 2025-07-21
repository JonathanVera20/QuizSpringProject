package com.example.demo.controller;

import com.example.demo.model.AuthenticationRequest;
import com.example.demo.model.User;
import com.example.demo.model.UserResponse;
import com.example.demo.repository.UserRepository;
import com.example.demo.config.TokenUtils;
import com.example.demo.config.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody AuthenticationRequest authenticationRequest) {
        try {
            // Validate input
            if (authenticationRequest.getUsername() == null || authenticationRequest.getPassword() == null) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "Username and password are required");
                return ResponseEntity.badRequest().body(error);
            }

            // Sanitize input
            String username = SecurityUtils.sanitizeInput(authenticationRequest.getUsername());
            String password = authenticationRequest.getPassword();

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwt = tokenUtils.generateToken(userDetails);

            Optional<User> user = userRepository.findByUsername(userDetails.getUsername());

            Map<String, Object> response = new HashMap<>();
            response.put("token", jwt);
            response.put("type", "Bearer");
            response.put("user", user.map(UserResponse::new).orElse(null));
            response.put("message", "Login successful");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Invalid username or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            // Debug logging
            System.out.println("Received user object: " + user);
            System.out.println("Username: " + user.getUsername());
            System.out.println("Email: " + user.getEmail());
            System.out.println("Password: " + (user.getPassword() != null ? "[PROVIDED]" : "[NULL]"));

            // Validate input
            if (user.getUsername() == null || user.getEmail() == null || user.getPassword() == null) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "Username, email, and password are required");
                return ResponseEntity.badRequest().body(error);
            }

            // Validate and sanitize input
            String username = SecurityUtils.validateAndSanitizeUsername(user.getUsername());
            String email = SecurityUtils.validateAndSanitizeEmail(user.getEmail());

            if (!SecurityUtils.meetsPasswordRequirements(user.getPassword())) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "Password must be at least 6 characters long and contain both letters and numbers");
                return ResponseEntity.badRequest().body(error);
            }

            // Check if username already exists
            if (userRepository.findByUsername(username).isPresent()) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "Username already exists");
                return ResponseEntity.badRequest().body(error);
            }

            // Check if email already exists
            if (userRepository.findByEmail(email).isPresent()) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "Email already exists");
                return ResponseEntity.badRequest().body(error);
            }

            // Set sanitized values
            user.setUsername(username);
            user.setEmail(email);

            // Set default role if not provided
            if (user.getRole() == null || user.getRole().trim().isEmpty()) {
                user.setRole("USER");
            }

            // Encode password
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            User savedUser = userRepository.save(user);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "User registered successfully");
            response.put("user", new UserResponse(savedUser));

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Registration failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String token) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "Invalid token format");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            String jwt = token.substring(7);

            if (!SecurityUtils.isValidJwtFormat(jwt)) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "Invalid token format");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            String username = tokenUtils.getUsernameFromToken(jwt);

            if (username == null || !tokenUtils.validateToken(jwt)) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "Invalid or expired token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "User not found");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            User user = userOpt.get();
            UserDetails userDetails = org.springframework.security.core.userdetails.User
                    .withUsername(user.getUsername())
                    .password(user.getPassword())
                    .authorities("ROLE_USER")
                    .build();

            String newToken = tokenUtils.generateToken(userDetails);
            Map<String, Object> response = new HashMap<>();
            response.put("token", newToken);
            response.put("type", "Bearer");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Token refresh failed");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String token) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "Invalid token format");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            String jwt = token.substring(7);

            if (!SecurityUtils.isValidJwtFormat(jwt)) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "Invalid token format");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            String username = tokenUtils.getUsernameFromToken(jwt);

            if (username == null || !tokenUtils.validateToken(jwt)) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "Invalid or expired token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            Optional<User> user = userRepository.findByUsername(username);
            if (user.isPresent()) {
                return ResponseEntity.ok(new UserResponse(user.get()));
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("message", "User not found");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Authentication failed");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }
}
