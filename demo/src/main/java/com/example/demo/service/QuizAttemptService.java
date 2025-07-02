package com.example.demo.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Quiz;
import com.example.demo.model.QuizAttempt;
import com.example.demo.model.User;
import com.example.demo.repository.QuizAttemptRepository;

@Service
public class QuizAttemptService {

    @Autowired
    private QuizAttemptRepository quizAttemptRepository;

    // Obtener todos los intentos de quiz
    public List<QuizAttempt> getAllQuizAttempts() {
        return quizAttemptRepository.findAll();
    }

    // Obtener un intento de quiz por su ID
    public QuizAttempt getQuizAttemptById(Long id) {
        return quizAttemptRepository.findById(id).orElse(null);
    }

    // Crear un nuevo intento de quiz
    public QuizAttempt createQuizAttempt(QuizAttempt quizAttempt) {
        return quizAttemptRepository.save(quizAttempt);
    }

    // Actualizar un intento de quiz por su ID
    public QuizAttempt updateQuizAttempt(QuizAttempt quizAttempt) {
        return quizAttemptRepository.save(quizAttempt);
    }

    // Eliminar un intento de quiz por su ID
    public void deleteQuizAttempt(Long id) {
        quizAttemptRepository.deleteById(id);
    }

    // Obtener intentos de quiz de un usuario específico
    public List<QuizAttempt> getQuizAttemptsByUser(User user) {
        return quizAttemptRepository.findByUser(user);
    }

    // Obtener intentos de quiz para un quiz específico
    public List<QuizAttempt> getQuizAttemptsByQuiz(Quiz quiz) {
        return quizAttemptRepository.findByQuiz(quiz);
    }

    // Obtener intentos de quiz realizados en una fecha específica
    public List<QuizAttempt> getQuizAttemptsByDate(Date date) {
        return quizAttemptRepository.findByDate(date);
    }
}
