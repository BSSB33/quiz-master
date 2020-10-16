package com.quizmaster.backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/quizzes")
public class QuizController {

    @PostMapping("/")
    public ResponseEntity createQuiz(){
        return ResponseEntity.ok().build();
    }

}
