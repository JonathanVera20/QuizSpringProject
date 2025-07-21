package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Answers;
import com.example.demo.service.AnswersService;

@RestController
@RequestMapping("api/answers")
public class AnswersController {

    @Autowired
    private AnswersService answersService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Answers createAnswer(@RequestBody Answers answer) {
        return answersService.createAnswer(answer);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Answers getAnswerById(@PathVariable Long id) {
        return answersService.getAnswerById(id);
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Answers updateAnswer(@RequestBody Answers answer) {
        return answersService.updateAnswer(answer);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteAnswer(@PathVariable Long id) {
        answersService.deleteAnswer(id);
    }

    @GetMapping("/question/{questionId}")
    public List<Answers> getAnswersByQuestionId(@PathVariable Long questionId) {
        return answersService.getAnswersByQuestionId(questionId);
    }
}
