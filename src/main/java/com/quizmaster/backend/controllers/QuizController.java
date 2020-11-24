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

    @GetMapping("") //For testing
    public ResponseEntity getById() {
        return ResponseEntity.ok(quizMongoRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity getById(@PathVariable String id) {
        if (quizMongoRepository.existsById(id)) {
            return ResponseEntity.ok(quizMongoRepository.getById(id));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("")
    public ResponseEntity postById(@RequestBody Quiz quiz) {
        if (quiz.getId() != null && quizMongoRepository.existsById(quiz.getId())) { // ERROR: getId should not be called before save!
            return ResponseEntity.badRequest().body("ID Taken!");
        }
        if(quiz.getCreatedAt() == null) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(quizMongoRepository.save(quiz));
    }

    @PutMapping("/{id}")
    public ResponseEntity putById(@PathVariable String id, @RequestBody Quiz quizToSave) {
        if (quizMongoRepository.existsById(id)) {
            quizToSave.setId(id);
            Quiz oldQuiz = quizMongoRepository.getById(id);
            quizToSave.setCreatedAt(oldQuiz.getCreatedAt());
            return ResponseEntity.ok(quizMongoRepository.save(quizToSave));
        }
        if(quizToSave.getCreatedAt() == null) return ResponseEntity.noContent().build();
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteById(@PathVariable String id) {
        if (quizMongoRepository.existsById(id)) {
            quizMongoRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }


}
