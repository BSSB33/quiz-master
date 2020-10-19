package com.quizmaster.backend.controllers;

import com.quizmaster.backend.entities.Quiz;
import com.quizmaster.backend.repositories.QuizMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/quizzes")
public class QuizController {

    @Autowired
    private QuizMongoRepository quizMongoRepository;

    @GetMapping("/{id}")
    public ResponseEntity getById(@PathVariable String id){
        return ResponseEntity.ok(quizMongoRepository.getById(id));
    }

    @PostMapping("")
    public ResponseEntity postById(@RequestBody Quiz quiz){
        return ResponseEntity.ok(quizMongoRepository.save(quiz));
    }

}
