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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Quiz;
import com.example.demo.model.QuizAttempt;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.QuizAttemptService;
import com.example.demo.service.QuizService;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @Autowired
    private QuizAttemptService quizAttemptService;

    @Autowired
    private UserRepository userRepository;

    // GET: Obtiene todos los quizzes (cualquier usuario autenticado)
    @GetMapping
    public ResponseEntity<List<Quiz>> getAllQuizzes() {
        List<Quiz> quizzes = quizService.getAllQuizzes();
        return ResponseEntity.ok(quizzes);
    }

    // GET: Obtiene un quiz por su ID
    @GetMapping("/{id}")
    public ResponseEntity<Quiz> getQuizById(@PathVariable Long id) {
        Quiz quiz = quizService.getQuizById(id);
        if (quiz != null) {
            return ResponseEntity.ok(quiz);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // POST: Crea un nuevo quiz (solo ADMIN)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Quiz> createQuiz(@RequestBody Quiz quiz) {
        Quiz createdQuiz = quizService.createQuiz(quiz);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdQuiz);
    }

    // PUT: Actualiza un quiz por su ID (solo ADMIN)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Quiz> updateQuiz(@PathVariable Long id, @RequestBody Quiz newQuiz) {
        Quiz updatedQuiz = quizService.updateQuiz(newQuiz);
        if (updatedQuiz != null) {
            return ResponseEntity.ok(updatedQuiz);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // DELETE: Elimina un quiz por su ID (solo ADMIN)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long id) {
        quizService.deleteQuiz(id);
        return ResponseEntity.noContent().build();
    }

    // GET: Obtiene quizzes por nivel de dificultad
    @GetMapping("/difficulty/{level}")
    public ResponseEntity<List<Quiz>> getQuizzesByDifficultyLevel(@PathVariable String level) {
        List<Quiz> quizzes = quizService.getQuizzesByDifficultyLevel(level);
        return ResponseEntity.ok(quizzes);
    }

    // GET: Encuentra un quiz por su título
    @GetMapping("/title/{title}")
    public ResponseEntity<Quiz> getQuizByTitle(@PathVariable String title) {
        Quiz quiz = quizService.getQuizByTitle(title);
        if (quiz != null) {
            return ResponseEntity.ok(quiz);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // POST: Submits a quiz attempt (more intuitive than /quizAttempts)
    @PostMapping("/{id}/attempt")
    public ResponseEntity<?> submitQuizAttempt(@PathVariable Long id, @RequestBody QuizAttempt quizAttempt) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();

        Optional<User> currentUserOpt = userRepository.findByUsername(currentUsername);
        if (currentUserOpt.isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Current user not found");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        User currentUser = currentUserOpt.get();
        Quiz quiz = quizService.getQuizById(id);

        if (quiz == null) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Quiz not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        // Set the user and quiz for the attempt
        quizAttempt.setUser(currentUser);
        quizAttempt.setQuiz(quiz);

        QuizAttempt createdAttempt = quizAttemptService.createQuizAttempt(quizAttempt);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAttempt);
    }
}
