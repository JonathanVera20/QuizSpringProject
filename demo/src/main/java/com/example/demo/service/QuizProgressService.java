package com.example.demo.service;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Quiz;
import com.example.demo.model.QuizAttempt;
import com.example.demo.model.QuizProgress;
import com.example.demo.repository.QuizProgressRepository;

@Service
public class QuizProgressService {

    @Autowired
    private QuizProgressRepository quizProgressRepository;

    // Obtener todos los progresos de quiz
    public List<QuizProgress> getAllQuizProgresses() {
        return quizProgressRepository.findAll();
    }

    // Obtener un progreso de quiz por su ID
    public QuizProgress getQuizProgressById(Long id) {
        return quizProgressRepository.findById(id).orElse(null);
    }

    // Crear un nuevo progreso de quiz
    public QuizProgress createQuizProgress(QuizProgress quizProgress) {
        return quizProgressRepository.save(quizProgress);
    }

    // Actualizar un progreso de quiz por su ID
    public QuizProgress updateQuizProgress(QuizProgress quizProgress) {
        return quizProgressRepository.save(quizProgress);
    }

    // Eliminar un progreso de quiz por su ID
    public void deleteQuizProgress(Long id) {
        quizProgressRepository.deleteById(id);
    }

    // Obtener progresos de quiz de un intento de quiz específico
    public List<QuizProgress> getQuizProgressesByAttempt(QuizAttempt attempt) {
        return quizProgressRepository.findByAttempt(attempt);
    }

    // Obtener progresos de quiz para un quiz específico
    public List<QuizProgress> getQuizProgressesByQuiz(Quiz quiz) {
        return quizProgressRepository.findByQuiz(quiz);
    }
}
