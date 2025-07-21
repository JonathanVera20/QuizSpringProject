package com.example.demo.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.Quiz;
import com.example.demo.model.QuizAttempt;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.QuizAttemptService;

@RestController
@RequestMapping("/api/quizAttempts")
public class QuizAttemptController {

    @Autowired
    private QuizAttemptService quizAttemptService;

    @Autowired
    private UserRepository userRepository;

    // GET: Obtiene todos los intentos de quiz (filtrados por usuario)
    @GetMapping
    public ResponseEntity<List<QuizAttempt>> getAllQuizAttempts() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();

        Optional<User> currentUserOpt = userRepository.findByUsername(currentUsername);
        if (currentUserOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        User currentUser = currentUserOpt.get();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            // Admin puede ver todos los intentos
            return ResponseEntity.ok(quizAttemptService.getAllQuizAttempts());
        } else {
            // Usuario normal solo ve sus propios intentos
            return ResponseEntity.ok(quizAttemptService.getQuizAttemptsByUser(currentUser));
        }
    }

    // GET: Obtiene un intento de quiz por su ID (solo si pertenece al usuario o es admin)
    @GetMapping("/{id}")
    public ResponseEntity<?> getQuizAttemptById(@PathVariable Long id) {
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

        QuizAttempt quizAttempt = quizAttemptService.getQuizAttemptById(id);
        if (quizAttempt == null) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Quiz attempt not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        // Verificar que el intento pertenece al usuario actual o que es admin
        if (!isAdmin && !quizAttempt.getUser().getId().equals(currentUser.getId())) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Access denied: You can only view your own quiz attempts");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }

        return ResponseEntity.ok(quizAttempt);
    }

    // POST: Crea un nuevo intento de quiz (el usuario actual)
    @PostMapping
    public ResponseEntity<?> createQuizAttempt(@RequestBody QuizAttempt quizAttempt) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();

        Optional<User> currentUserOpt = userRepository.findByUsername(currentUsername);
        if (currentUserOpt.isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Current user not found");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        User currentUser = currentUserOpt.get();

        // Asegurar que el intento se cree para el usuario actual
        quizAttempt.setUser(currentUser);

        QuizAttempt createdAttempt = quizAttemptService.createQuizAttempt(quizAttempt);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAttempt);
    }

    // GET: Obtiene intentos de quiz de un usuario específico (solo admin o el propio usuario)
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getQuizAttemptsByUser(@PathVariable Long userId) {
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

        // Verificar que es admin o que está viendo sus propios intentos
        if (!isAdmin && !currentUser.getId().equals(userId)) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Access denied: You can only view your own quiz attempts");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }

        User targetUser = new User();
        targetUser.setId(userId);
        List<QuizAttempt> attempts = quizAttemptService.getQuizAttemptsByUser(targetUser);
        return ResponseEntity.ok(attempts);
    }

    // GET: Obtiene intentos de quiz para un quiz específico (filtrados por usuario)
    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<List<QuizAttempt>> getQuizAttemptsByQuiz(@PathVariable Long quizId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();

        Optional<User> currentUserOpt = userRepository.findByUsername(currentUsername);
        if (currentUserOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        User currentUser = currentUserOpt.get();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        Quiz quiz = new Quiz();
        quiz.setId(quizId);
        List<QuizAttempt> attempts = quizAttemptService.getQuizAttemptsByQuiz(quiz);

        if (!isAdmin) {
            // Filtrar solo los intentos del usuario actual
            attempts = attempts.stream()
                    .filter(attempt -> attempt.getUser().getId().equals(currentUser.getId()))
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok(attempts);
    }

    // GET: Obtiene intentos de quiz realizados en una fecha específica (filtrados por usuario)
    @GetMapping("/date/{date}")
    public ResponseEntity<List<QuizAttempt>> getQuizAttemptsByDate(@PathVariable Date date) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();

        Optional<User> currentUserOpt = userRepository.findByUsername(currentUsername);
        if (currentUserOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        User currentUser = currentUserOpt.get();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        List<QuizAttempt> attempts = quizAttemptService.getQuizAttemptsByDate(date);

        if (!isAdmin) {
            // Filtrar solo los intentos del usuario actual
            attempts = attempts.stream()
                    .filter(attempt -> attempt.getUser().getId().equals(currentUser.getId()))
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok(attempts);
    }

    // GET: Obtiene los resultados (respuestas correctas/incorrectas) de un intento específico
    @GetMapping("/{id}/results")
    public ResponseEntity<?> getQuizAttemptResults(@PathVariable Long id) {
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

        QuizAttempt quizAttempt = quizAttemptService.getQuizAttemptById(id);
        if (quizAttempt == null) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Quiz attempt not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        // Verificar que el intento pertenece al usuario actual o que es admin
        if (!isAdmin && !quizAttempt.getUser().getId().equals(currentUser.getId())) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Access denied: You can only view your own quiz attempt results");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }

        // Return the quiz attempt with full details for results viewing
        // This could be enhanced with additional result information if needed
        Map<String, Object> results = new HashMap<>();
        results.put("attempt", quizAttempt);
        results.put("quiz", quizAttempt.getQuiz());
        results.put("score", quizAttempt.getScore());
        results.put("attemptDate", quizAttempt.getDate());
        results.put("message", "Quiz attempt results retrieved successfully");

        return ResponseEntity.ok(results);
    }
}
