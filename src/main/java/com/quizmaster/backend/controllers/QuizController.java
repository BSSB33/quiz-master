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

//    @PostMapping("/")
//    public ResponseEntity createQuiz(){
//        return ResponseEntity.ok().build();
//    }

    @GetMapping("/{id}")
    public ResponseEntity getById(@PathVariable String id){
        return ResponseEntity.ok(quizMongoRepository.getById(id));
    }

    @PostMapping("add")
    public ResponseEntity postById(@RequestBody Quiz quiz){
        System.out.println("POST CALLED");
        System.out.println(quiz.toString());
        return ResponseEntity.ok(quizMongoRepository.save(quiz));
    }

}
