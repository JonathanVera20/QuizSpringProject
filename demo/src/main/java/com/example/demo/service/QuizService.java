package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Quiz;
import com.example.demo.repository.QuizRepository;

@Service
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    // Obtener todos los quizzes
    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }

    // Obtener un quiz por su ID
    public Quiz getQuizById(Long id) {
        return quizRepository.findById(id).orElse(null);
    }

    // Crear un nuevo quiz
    public Quiz createQuiz(Quiz quiz) {
        return quizRepository.save(quiz);
    }

    // Actualizar un quiz por su ID
    public Quiz updateQuiz(Quiz quiz) {
        return quizRepository.save(quiz);
    }

    // Eliminar un quiz por su ID
    public void deleteQuiz(Long id) {
        quizRepository.deleteById(id);
    }

    public List<Quiz> getQuizzesByDifficultyLevel(String difficultyLevel) {
        return quizRepository.findByDifficultyLevel(difficultyLevel);
    }

    // Encontrar un quiz por su título
    public Quiz getQuizByTitle(String title) {
        return quizRepository.findByTitle(title);
    }
}
