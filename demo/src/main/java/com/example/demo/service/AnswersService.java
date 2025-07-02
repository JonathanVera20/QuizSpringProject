package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Answers;
import com.example.demo.repository.AnswersRepository;

@Service
public class AnswersService {

    @Autowired
    private AnswersRepository answersRepository;

    // Crear una nueva respuesta
    public Answers createAnswer(Answers answer) {
        return answersRepository.save(answer);
    }

    // Obtener una respuesta por su ID
    public Answers getAnswerById(Long id) {
        return answersRepository.findById(id).orElse(null);
    }

    // Obtener todas las respuestas
    public List<Answers> getAllAnswers() {
        return answersRepository.findAll();
    }

    // Actualizar una respuesta por su ID
    public Answers updateAnswer(Answers answer) {
        return answersRepository.save(answer);
    }

    // Eliminar una respuesta por su ID
    public void deleteAnswer(Long id) {
        answersRepository.deleteById(id);
    }

    // Encuentra todas las respuestas para una pregunta específica
    public List<Answers> getAnswersByQuestionId(Long questionId) {
        return answersRepository.findByQuestionId(questionId);
    }

    // Encuentra todas las respuestas correctas
    public List<Answers> getAnswersByIsCorrect(Integer isCorrect) {
        return answersRepository.findByIsCorrect(isCorrect);
    }
}
