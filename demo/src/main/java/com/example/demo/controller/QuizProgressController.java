package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.Quiz;
import com.example.demo.model.QuizAttempt;
import com.example.demo.model.QuizProgress;
import com.example.demo.service.QuizProgressService;

@RestController
@RequestMapping("/api/quizProgresses")
public class QuizProgressController {

    @Autowired
    private QuizProgressService quizProgressService;

    // GET: Obtiene todos los progresos de quiz
    @GetMapping
    public List<QuizProgress> getAllQuizProgresses() {
        return quizProgressService.getAllQuizProgresses();
    }

    // GET: Obtiene un progreso de quiz por su ID
    @GetMapping("/{id}")
    public ResponseEntity<QuizProgress> getQuizProgressById(@PathVariable Long id) {
        QuizProgress quizProgress = quizProgressService.getQuizProgressById(id);
        if (quizProgress != null) {
            return ResponseEntity.ok(quizProgress);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // POST: Crea un nuevo progreso de quiz
    @PostMapping
    public QuizProgress createQuizProgress(@RequestBody QuizProgress quizProgress) {
        return quizProgressService.createQuizProgress(quizProgress);
    }

    // PUT: Actualiza un progreso de quiz por su ID
    @PutMapping("/{id}")
    public ResponseEntity<QuizProgress> updateQuizProgress(@PathVariable Long id, @RequestBody QuizProgress newQuizProgress) {
        QuizProgress updatedQuizProgress = quizProgressService.updateQuizProgress(newQuizProgress);
        if (updatedQuizProgress != null) {
            return ResponseEntity.ok(updatedQuizProgress);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // DELETE: Elimina un progreso de quiz por su ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuizProgress(@PathVariable Long id) {
        quizProgressService.deleteQuizProgress(id);
        return ResponseEntity.noContent().build();
    }

    // GET: Obtiene progresos de quiz de un intento de quiz específico
    @GetMapping("/attempt/{attemptId}")
    public List<QuizProgress> getQuizProgressesByAttempt(@PathVariable Long attemptId) {
        QuizAttempt attempt = new QuizAttempt();
        attempt.setId(attemptId);
        return quizProgressService.getQuizProgressesByAttempt(attempt);
    }

    // GET: Obtiene progresos de quiz para un quiz específico
    @GetMapping("/quiz/{quizId}")
    public List<QuizProgress> getQuizProgressesByQuiz(@PathVariable Long quizId) {
        Quiz quiz = new Quiz();
        quiz.setId(quizId);
        return quizProgressService.getQuizProgressesByQuiz(quiz);
    }
}
