package com.example.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.User;
import com.example.demo.model.UserResponse;
import com.example.demo.repository.UserRepository;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // GET: Obtiene todos los usuarios (solo ADMIN)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserResponse> userResponses = users.stream()
                .map(UserResponse::new)
                .toList();
        return ResponseEntity.ok(userResponses);
    }

    // GET: Obtiene el perfil del usuario actual
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();

        Optional<User> currentUserOpt = userRepository.findByUsername(currentUsername);
        if (currentUserOpt.isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Current user not found");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        User currentUser = currentUserOpt.get();
        return ResponseEntity.ok(new UserResponse(currentUser));
    }

    // PUT: Actualiza el perfil del usuario actual
    @PutMapping("/me")
    public ResponseEntity<?> updateCurrentUser(@RequestBody User newUser) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();

        Optional<User> currentUserOpt = userRepository.findByUsername(currentUsername);
        if (currentUserOpt.isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Current user not found");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        User currentUser = currentUserOpt.get();

        try {
            // Actualizar campos básicos
            if (newUser.getUsername() != null && !newUser.getUsername().trim().isEmpty()) {
                // Verificar que el username no esté en uso por otro usuario
                Optional<User> userWithSameUsername = userRepository.findByUsername(newUser.getUsername());
                if (userWithSameUsername.isPresent() && !userWithSameUsername.get().getId().equals(currentUser.getId())) {
                    Map<String, String> error = new HashMap<>();
                    error.put("message", "Username already exists");
                    return ResponseEntity.badRequest().body(error);
                }
                currentUser.setUsername(newUser.getUsername());
            }

            if (newUser.getEmail() != null && !newUser.getEmail().trim().isEmpty()) {
                // Verificar que el email no esté en uso por otro usuario
                Optional<User> userWithSameEmail = userRepository.findByEmail(newUser.getEmail());
                if (userWithSameEmail.isPresent() && !userWithSameEmail.get().getId().equals(currentUser.getId())) {
                    Map<String, String> error = new HashMap<>();
                    error.put("message", "Email already exists");
                    return ResponseEntity.badRequest().body(error);
                }
                currentUser.setEmail(newUser.getEmail());
            }

            // Solo actualizar la contraseña si se proporciona
            if (newUser.getPassword() != null && !newUser.getPassword().trim().isEmpty()) {
                currentUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
            }

            // Los usuarios regulares no pueden cambiar su propio rol
            User updatedUser = userRepository.save(currentUser);
            return ResponseEntity.ok(new UserResponse(updatedUser));

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Error updating user: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // GET: Obtiene un usuario por su ID (solo el propio usuario o ADMIN)
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();

        Optional<User> currentUserOpt = userRepository.findByUsername(currentUsername);
        if (currentUserOpt.isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Current user not found");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        User currentUser = currentUserOpt.get();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // Solo permitir si es admin o es el propio usuario
        if (!isAdmin && !currentUser.getId().equals(id)) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Access denied: You can only view your own profile");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }

        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(new UserResponse(user.get()));
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("message", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    // POST: Crea un nuevo usuario (solo ADMIN)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createUser(@RequestBody User user) {
        try {
            // Verificar si el usuario ya existe
            if (userRepository.findByUsername(user.getUsername()).isPresent()) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "Username already exists");
                return ResponseEntity.badRequest().body(null);
            }

            if (userRepository.findByEmail(user.getEmail()).isPresent()) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "Email already exists");
                return ResponseEntity.badRequest().body(null);
            }

            // Encriptar contraseña
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            // Establecer rol por defecto si no se especifica
            if (user.getRole() == null || user.getRole().trim().isEmpty()) {
                user.setRole("USER");
            }

            User savedUser = userRepository.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(new UserResponse(savedUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // PUT: Actualiza un usuario existente (solo el propio usuario o ADMIN)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User newUser) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();

        Optional<User> currentUserOpt = userRepository.findByUsername(currentUsername);
        if (currentUserOpt.isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Current user not found");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        User currentUser = currentUserOpt.get();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // Solo permitir si es admin o es el propio usuario
        if (!isAdmin && !currentUser.getId().equals(id)) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Access denied: You can only update your own profile");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }

        Optional<User> existingUserOpt = userRepository.findById(id);
        if (existingUserOpt.isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        User existingUser = existingUserOpt.get();

        try {
            // Actualizar campos básicos
            if (newUser.getUsername() != null && !newUser.getUsername().trim().isEmpty()) {
                // Verificar que el username no esté en uso por otro usuario
                Optional<User> userWithSameUsername = userRepository.findByUsername(newUser.getUsername());
                if (userWithSameUsername.isPresent() && !userWithSameUsername.get().getId().equals(id)) {
                    Map<String, String> error = new HashMap<>();
                    error.put("message", "Username already exists");
                    return ResponseEntity.badRequest().body(error);
                }
                existingUser.setUsername(newUser.getUsername());
            }

            if (newUser.getEmail() != null && !newUser.getEmail().trim().isEmpty()) {
                // Verificar que el email no esté en uso por otro usuario
                Optional<User> userWithSameEmail = userRepository.findByEmail(newUser.getEmail());
                if (userWithSameEmail.isPresent() && !userWithSameEmail.get().getId().equals(id)) {
                    Map<String, String> error = new HashMap<>();
                    error.put("message", "Email already exists");
                    return ResponseEntity.badRequest().body(error);
                }
                existingUser.setEmail(newUser.getEmail());
            }

            // Solo actualizar la contraseña si se proporciona
            if (newUser.getPassword() != null && !newUser.getPassword().trim().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
            }

            // Solo admins pueden cambiar roles
            if (isAdmin && newUser.getRole() != null && !newUser.getRole().trim().isEmpty()) {
                existingUser.setRole(newUser.getRole());
            }

            User updatedUser = userRepository.save(existingUser);
            return ResponseEntity.ok(new UserResponse(updatedUser));

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Error updating user: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // DELETE: Elimina un usuario por su ID (solo ADMIN)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        userRepository.deleteById(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "User deleted successfully");
        return ResponseEntity.ok(response);
    }
}
