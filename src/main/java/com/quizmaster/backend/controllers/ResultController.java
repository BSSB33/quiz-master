package com.quizmaster.backend.controllers;

import com.quizmaster.backend.entities.QuizGame;
import com.quizmaster.backend.repositories.QuizGameMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/result")
@CrossOrigin
public class ResultController {


    @Autowired
    private QuizGameMongoRepository quizGameMongoRepository;

    @GetMapping("")
    public ResponseEntity getAll() {
        List<QuizGame> collect = new ArrayList<QuizGame>();

        for (QuizGame act : quizGameMongoRepository.findAll()) {
            if (act.getQuiz().getOwnerId().equals(getUsername())) {
                collect.add(act);
            }
        }
        return ResponseEntity.ok(collect);
    }

    @GetMapping("/{id}")
    public ResponseEntity getById(@PathVariable String id) {

        if (quizGameMongoRepository.existsById(id)) {
            QuizGame toRetrieve = quizGameMongoRepository.getById(id);
            if (toRetrieve.getQuiz().getOwnerId().equals(getUsername())) {
                return ResponseEntity.ok(toRetrieve);
            }
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteById(@PathVariable String id) {
        if (quizGameMongoRepository.existsById(id)) {
            QuizGame retrieved = quizGameMongoRepository.getById(id);

            if (retrieved.getQuiz().getOwnerId().equals(getUsername())) {
                quizGameMongoRepository.deleteById(id);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(403).build();
            }
        }
        return ResponseEntity.notFound().build();
    }

    private String getUsername() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        String username = authentication.getName();
        return username;
    }
}
