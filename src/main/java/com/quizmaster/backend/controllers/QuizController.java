package com.quizmaster.backend.controllers;

import com.quizmaster.backend.repositories.QuizMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/quizzes")
public class QuizController {

    @Autowired
    private QuizMongoRepository quizMongoRepository;

    @PostMapping("/")
    public ResponseEntity createQuiz(){
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity getById(@PathVariable String id){
        System.out.println("QUIZ: " + quizMongoRepository.getById(id).toString());
        return ResponseEntity.ok(quizMongoRepository.getById(id));
    }

}
