package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Questions;
import com.example.demo.repository.QuestionsRepository;

@Service
public class QuestionsService {

    @Autowired
    private QuestionsRepository questionsRepository;

    // Crear una nueva pregunta
    public Questions createQuestion(Questions question) {
        return questionsRepository.save(question);
    }

    // Obtener una pregunta por su ID
    public Questions getQuestionById(Long id) {
        return questionsRepository.findById(id).orElse(null);
    }

    // Obtener todas las preguntas
    public List<Questions> getAllQuestions() {
        return questionsRepository.findAll();
    }

    // Actualizar una pregunta por su ID
    public Questions updateQuestion(Questions question) {
        return questionsRepository.save(question);
    }

    // Eliminar una pregunta por su ID
    public void deleteQuestion(Long id) {
        questionsRepository.deleteById(id);
    }
}
