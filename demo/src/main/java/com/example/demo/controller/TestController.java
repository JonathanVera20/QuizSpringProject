package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/public")
    public String testPublic(HttpServletRequest request) {
        return "Public endpoint working! URI: " + request.getRequestURI() + ", Method: " + request.getMethod();
    }
}
