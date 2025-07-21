package com.example.demo.controller;

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
import com.example.demo.model.QuizProgress;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.QuizProgressService;

@RestController
@RequestMapping("/api/quizProgresses")
public class QuizProgressController {

    @Autowired
    private QuizProgressService quizProgressService;

    @Autowired
    private UserRepository userRepository;

    // GET: Obtiene todos los progresos de quiz (filtrados por usuario)
    @GetMapping
    public ResponseEntity<List<QuizProgress>> getAllQuizProgresses() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();

        Optional<User> currentUserOpt = userRepository.findByUsername(currentUsername);
        if (currentUserOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        User currentUser = currentUserOpt.get();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        List<QuizProgress> progresses = quizProgressService.getAllQuizProgresses();

        if (isAdmin) {
            // Admin puede ver todos los progresos
            return ResponseEntity.ok(progresses);
        } else {
            // Usuario normal solo ve sus propios progresos
            List<QuizProgress> userProgresses = progresses.stream()
                    .filter(progress -> progress.getAttempt() != null
                    && progress.getAttempt().getUser() != null
                    && progress.getAttempt().getUser().getId().equals(currentUser.getId()))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(userProgresses);
        }
    }

    // GET: Obtiene un progreso de quiz por su ID (solo si pertenece al usuario o es admin)
    @GetMapping("/{id}")
    public ResponseEntity<?> getQuizProgressById(@PathVariable Long id) {
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

        QuizProgress quizProgress = quizProgressService.getQuizProgressById(id);
        if (quizProgress == null) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Quiz progress not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        // Verificar que el progreso pertenece al usuario actual o que es admin
        if (!isAdmin && (quizProgress.getAttempt() == null
                || quizProgress.getAttempt().getUser() == null
                || !quizProgress.getAttempt().getUser().getId().equals(currentUser.getId()))) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Access denied: You can only view your own quiz progress");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }

        return ResponseEntity.ok(quizProgress);
    }

    // POST: Crea un nuevo progreso de quiz (para el usuario actual)
    @PostMapping
    public ResponseEntity<?> createQuizProgress(@RequestBody QuizProgress quizProgress) {
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

        // Verificar que el intento asociado pertenece al usuario actual (o es admin)
        if (quizProgress.getAttempt() != null && quizProgress.getAttempt().getUser() != null) {
            if (!isAdmin && !quizProgress.getAttempt().getUser().getId().equals(currentUser.getId())) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "Access denied: You can only create progress for your own quiz attempts");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }
        }

        QuizProgress createdProgress = quizProgressService.createQuizProgress(quizProgress);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProgress);
    }

    // PUT: Actualiza un progreso de quiz por su ID (solo el propio usuario o ADMIN)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateQuizProgress(@PathVariable Long id, @RequestBody QuizProgress newQuizProgress) {
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

        QuizProgress existingProgress = quizProgressService.getQuizProgressById(id);
        if (existingProgress == null) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Quiz progress not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        // Verificar que el progreso pertenece al usuario actual o que es admin
        if (!isAdmin && (existingProgress.getAttempt() == null
                || existingProgress.getAttempt().getUser() == null
                || !existingProgress.getAttempt().getUser().getId().equals(currentUser.getId()))) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Access denied: You can only update your own quiz progress");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }

        QuizProgress updatedQuizProgress = quizProgressService.updateQuizProgress(newQuizProgress);
        if (updatedQuizProgress != null) {
            return ResponseEntity.ok(updatedQuizProgress);
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Failed to update quiz progress");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // DELETE: Elimina un progreso de quiz por su ID (solo el propio usuario o ADMIN)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteQuizProgress(@PathVariable Long id) {
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

        QuizProgress existingProgress = quizProgressService.getQuizProgressById(id);
        if (existingProgress == null) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Quiz progress not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        // Verificar que el progreso pertenece al usuario actual o que es admin
        if (!isAdmin && (existingProgress.getAttempt() == null
                || existingProgress.getAttempt().getUser() == null
                || !existingProgress.getAttempt().getUser().getId().equals(currentUser.getId()))) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Access denied: You can only delete your own quiz progress");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }

        quizProgressService.deleteQuizProgress(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Quiz progress deleted successfully");
        return ResponseEntity.ok(response);
    }

    // GET: Obtiene progresos de quiz de un intento de quiz específico (filtrado por usuario)
    @GetMapping("/attempt/{attemptId}")
    public ResponseEntity<List<QuizProgress>> getQuizProgressesByAttempt(@PathVariable Long attemptId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();

        Optional<User> currentUserOpt = userRepository.findByUsername(currentUsername);
        if (currentUserOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        User currentUser = currentUserOpt.get();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        QuizAttempt attempt = new QuizAttempt();
        attempt.setId(attemptId);
        List<QuizProgress> progresses = quizProgressService.getQuizProgressesByAttempt(attempt);

        if (!isAdmin) {
            // Filtrar solo los progresos del usuario actual
            progresses = progresses.stream()
                    .filter(progress -> progress.getAttempt() != null
                    && progress.getAttempt().getUser() != null
                    && progress.getAttempt().getUser().getId().equals(currentUser.getId()))
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok(progresses);
    }

    // GET: Obtiene progresos de quiz para un quiz específico (filtrado por usuario)
    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<List<QuizProgress>> getQuizProgressesByQuiz(@PathVariable Long quizId) {
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
        List<QuizProgress> progresses = quizProgressService.getQuizProgressesByQuiz(quiz);

        if (!isAdmin) {
            // Filtrar solo los progresos del usuario actual
            progresses = progresses.stream()
                    .filter(progress -> progress.getAttempt() != null
                    && progress.getAttempt().getUser() != null
                    && progress.getAttempt().getUser().getId().equals(currentUser.getId()))
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok(progresses);
    }
}
