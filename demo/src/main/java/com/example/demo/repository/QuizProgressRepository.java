package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Quiz;
import com.example.demo.model.QuizAttempt;
import com.example.demo.model.QuizProgress;

@Repository
public interface QuizProgressRepository extends JpaRepository<QuizProgress, Long> {

    // Encuentra todos los progresos de un intento de quiz específico
    List<QuizProgress> findByAttempt(QuizAttempt attempt);

    // Encuentra todos los progresos para un quiz específico
    List<QuizProgress> findByQuiz(Quiz quiz);

    // Encuentra todos los progresos que han sido completados
    List<QuizProgress> findByCompleted(Integer completed);

    // Encuentra todos los progresos con una puntuación específica
    List<QuizProgress> findByScore(Integer score);
}
