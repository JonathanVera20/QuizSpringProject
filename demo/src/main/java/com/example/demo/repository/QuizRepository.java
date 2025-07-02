package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Quiz;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    // Métodos CRUD básicos proporcionados por JpaRepository
    // save, findById, delete, etc.

    // Encuentra un quiz por su nivel de dificultad
    List<Quiz> findByDifficultyLevel(String difficultyLevel);

    // Encuentra un quiz por su título
    Quiz findByTitle(String title);
}

