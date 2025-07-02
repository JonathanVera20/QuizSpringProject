package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Story;
import com.example.demo.repository.StoryRepository;

@Service
public class StoryService {

    @Autowired
    private StoryRepository storyRepository;

    // Crear una nueva historia
    public Story createStory(Story story) {
        return storyRepository.save(story);
    }

    // Obtener una historia por su ID
    public Story getStoryById(Long id) {
        return storyRepository.findById(id).orElse(null);
    }

    // Obtener todas las historias
    public List<Story> getAllStories() {
        return storyRepository.findAll();
    }

    // Actualizar una historia por su ID
    public Story updateStory(Story story) {
        return storyRepository.save(story);
    }

    // Eliminar una historia por su ID
    public void deleteStory(Long id) {
        storyRepository.deleteById(id);
    }

    // Encuentra todas las historias para un quiz específico
    public List<Story> getStoriesByQuizId(Long quizId) {
        return storyRepository.findByQuizId(quizId);
    }

    // Encuentra una historia por su título
    public Story getStoryByTitle(String title) {
        return storyRepository.findByTitle(title);
    }

    // Encuentra una historia por su autor
    public Story getStoryByAuthor(String author) {
        return storyRepository.findByAuthor(author);
    }
}
