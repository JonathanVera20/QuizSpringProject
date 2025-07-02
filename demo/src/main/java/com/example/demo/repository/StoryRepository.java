package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Story;

@Repository
public interface StoryRepository extends JpaRepository<Story, Long> {
    // Métodos CRUD básicos proporcionados por JpaRepository
    // save, findById, delete, etc.

    // Encuentra todas las historias para un quiz específico
    List<Story> findByQuizId(Long quizId);

    // Encuentra una historia por su título
    Story findByTitle(String title);

    // Encuentra una historia por su autor
    Story findByAuthor(String author);
}
