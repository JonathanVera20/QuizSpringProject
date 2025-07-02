package com.example.demo.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Quiz;
import com.example.demo.model.QuizAttempt;
import com.example.demo.model.User;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {

    // Encuentra todos los intentos de un usuario específico
    List<QuizAttempt> findByUser(User user);

    // Encuentra todos los intentos para un quiz específico
    List<QuizAttempt> findByQuiz(Quiz quiz);

    // Encuentra todos los intentos realizados en una fecha específica
    List<QuizAttempt> findByDate(Date date);


}

