package com.quizmaster.backend.controllers;

import com.quizmaster.backend.entities.Quiz;
import com.quizmaster.backend.repositories.QuizMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/quizzes")
public class QuizController {


    @Autowired
    private QuizMongoRepository quizMongoRepository;

    @GetMapping("") //lists all quizzes for certain user
    public ResponseEntity getById() {

        List<Quiz> collect = new ArrayList<Quiz>();

        for (Quiz act : quizMongoRepository.findAll()) {
            if (act.getOwnerId().equals(getUsername())) {
                collect.add(act);
            }
        }

        return ResponseEntity.ok(collect);
    }

    @GetMapping("/{id}")
    public ResponseEntity getById(@PathVariable String id) {

        if (quizMongoRepository.existsById(id)) {
            Quiz toRetrieve = quizMongoRepository.getById(id);
            if (toRetrieve.getOwnerId().equals(getUsername())) {
                return ResponseEntity.ok(toRetrieve);
            }
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("")
    public ResponseEntity postById(@RequestBody Quiz quiz) {
        if (quiz.getId() != null && quizMongoRepository.existsById(quiz.getId())) { // ERROR: getId should not be called before save!
            return ResponseEntity.badRequest().body("ID Taken!");
        }
        if (nullChecker(quiz)) {
            quiz.setOwnerId(getUsername());
            return ResponseEntity.ok(quizMongoRepository.save(quiz));
        } else {
            return ResponseEntity.noContent().build();
        }

    }

    @PutMapping("/{id}")
    public ResponseEntity putById(@PathVariable String id, @RequestBody Quiz quizToSave) {
        if (!nullChecker(quizToSave)) return ResponseEntity.noContent().build();
        if (quizMongoRepository.existsById(id)) {
            Quiz retrieved = quizMongoRepository.getById(id);

            if (retrieved.getOwnerId().equals(getUsername())) {
                quizToSave.setId(id);
                Quiz oldQuiz = quizMongoRepository.getById(id);
                quizToSave.setCreatedAt(oldQuiz.getCreatedAt());
                quizToSave.setOwnerId(getUsername());
                return ResponseEntity.ok(quizMongoRepository.save(quizToSave));
            } else {
                return ResponseEntity.status(403).build();
            }
        }
        if (quizToSave.getCreatedAt() == null) return ResponseEntity.noContent().build();
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteById(@PathVariable String id) {
        if (quizMongoRepository.existsById(id)) {
            Quiz retrieved = quizMongoRepository.getById(id);
            if (retrieved.getOwnerId().equals(getUsername())) {
                quizMongoRepository.deleteById(id);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(403).build();
            }
        }
        return ResponseEntity.notFound().build();
    }

    private boolean nullChecker(Quiz quiz) {
        if (quiz.getTitle() == null || quiz.getDescription() == null || quiz.getCreatedAt() == null || quiz.getStartingTime() == null || quiz.getNotes() == null || quiz.getQuestions() == null) {
            return false;
        }
        return true;
    }

    private String getUsername() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        return authentication.getName();
    }
}
