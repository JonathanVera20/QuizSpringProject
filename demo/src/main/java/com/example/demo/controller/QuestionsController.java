package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Questions;
import com.example.demo.service.QuestionsService;

@RestController
@RequestMapping("/api/questions")
public class QuestionsController {

    @Autowired
    private QuestionsService questionsService;

    // GET: Obtiene todas las preguntas
    @GetMapping
    public List<Questions> getAllQuestions() {
        return questionsService.getAllQuestions();
    }

    // GET: Obtiene una pregunta por su ID
    @GetMapping("/{id}")
    public ResponseEntity<Questions> getQuestionById(@PathVariable Long id) {
        Questions question = questionsService.getQuestionById(id);
        if (question != null) {
            return ResponseEntity.ok(question);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // POST: Crea una nueva pregunta (solo ADMIN)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Questions createQuestion(@RequestBody Questions question) {
        return questionsService.createQuestion(question);
    }

    // PUT: Actualiza una pregunta por su ID (solo ADMIN)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Questions> updateQuestion(@PathVariable Long id, @RequestBody Questions newQuestion) {
        Questions updatedQuestion = questionsService.updateQuestion(newQuestion);
        if (updatedQuestion != null) {
            return ResponseEntity.ok(updatedQuestion);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // DELETE: Elimina una pregunta por su ID (solo ADMIN)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        questionsService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }
}
