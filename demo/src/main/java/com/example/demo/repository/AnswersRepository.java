package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Answers;

@Repository
public interface AnswersRepository extends JpaRepository<Answers, Long> {
    // Métodos CRUD básicos proporcionados por JpaRepository
    // save, findById, delete, etc.

    // Encuentra todas las respuestas para una pregunta específica
    List<Answers> findByQuestionId(Long questionId);

    // Encuentra todas las respuestas correctas
    List<Answers> findByIsCorrect(Integer isCorrect);
}