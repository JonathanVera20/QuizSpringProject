package com.example.demo.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.Quiz;
import com.example.demo.model.QuizAttempt;
import com.example.demo.model.User;
import com.example.demo.service.QuizAttemptService;

@RestController
@RequestMapping("/api/quizAttempts")
public class QuizAttemptController {

    @Autowired
    private QuizAttemptService quizAttemptService;

    // GET: Obtiene todos los intentos de quiz
    @GetMapping
    public List<QuizAttempt> getAllQuizAttempts() {
        return quizAttemptService.getAllQuizAttempts();
    }

    // GET: Obtiene un intento de quiz por su ID
    @GetMapping("/{id}")
    public ResponseEntity<QuizAttempt> getQuizAttemptById(@PathVariable Long id) {
        QuizAttempt quizAttempt = quizAttemptService.getQuizAttemptById(id);
        if (quizAttempt != null) {
            return ResponseEntity.ok(quizAttempt);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // POST: Crea un nuevo intento de quiz
    @PostMapping
    public QuizAttempt createQuizAttempt(@RequestBody QuizAttempt quizAttempt) {
        return quizAttemptService.createQuizAttempt(quizAttempt);
    }

    // PUT: Actualiza un intento de quiz por su ID
    @PutMapping("/{id}")
    public ResponseEntity<QuizAttempt> updateQuizAttempt(@PathVariable Long id, @RequestBody QuizAttempt newQuizAttempt) {
        QuizAttempt updatedQuizAttempt = quizAttemptService.updateQuizAttempt(newQuizAttempt);
        if (updatedQuizAttempt != null) {
            return ResponseEntity.ok(updatedQuizAttempt);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // DELETE: Elimina un intento de quiz por su ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuizAttempt(@PathVariable Long id) {
        quizAttemptService.deleteQuizAttempt(id);
        return ResponseEntity.noContent().build();
    }

    // GET: Obtiene intentos de quiz de un usuario específico
    @GetMapping("/user/{userId}")
    public List<QuizAttempt> getQuizAttemptsByUser(@PathVariable Long userId) {
        User user = new User();
        user.setId(userId);
        return quizAttemptService.getQuizAttemptsByUser(user);
    }

    // GET: Obtiene intentos de quiz para un quiz específico
    @GetMapping("/quiz/{quizId}")
    public List<QuizAttempt> getQuizAttemptsByQuiz(@PathVariable Long quizId) {
        Quiz quiz = new Quiz();
        quiz.setId(quizId);
        return quizAttemptService.getQuizAttemptsByQuiz(quiz);
    }

    // GET: Obtiene intentos de quiz realizados en una fecha específica
    @GetMapping("/date/{date}")
    public List<QuizAttempt> getQuizAttemptsByDate(@PathVariable Date date) {
        return quizAttemptService.getQuizAttemptsByDate(date);
    }
}
