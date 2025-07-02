package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Questions;
@Repository
public interface QuestionsRepository extends JpaRepository<Questions, Long> {
    // Métodos CRUD básicos proporcionados por JpaRepository
    // save, findById, delete, etc.

    // Encuentra preguntas por su texto
    List<Questions> findByText(String text);

    List<Questions> findByQuizId(Long quizId);

    // Elimina una pregunta por su texto
    void deleteByText(String text);

    // Encuentra todas las preguntas ordenadas por texto
    List<Questions> findAllByOrderByText();
}
