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

    @GetMapping("") //For testing
    public ResponseEntity getById() {

        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        String username = authentication.getName();

        List<Quiz> collect = new ArrayList<Quiz>();

        for (Quiz act : quizMongoRepository.findAll()){
            if (act.getOwnerId().equals(username)){
                collect.add(act);
            }
        }

        return ResponseEntity.ok(collect);
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
        if(!nullChecker(quiz)){
            return ResponseEntity.noContent().build();
        }

        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        String username = authentication.getName();

        quiz.setOwnerId(username);
        return ResponseEntity.ok(quizMongoRepository.save(quiz));
    }

    @PutMapping("/{id}")
    public ResponseEntity putById(@PathVariable String id, @RequestBody Quiz quizToSave) {
        if(!nullChecker(quizToSave)){
            return ResponseEntity.noContent().build();
        }
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
        return ResponseEntity.notFound().build();
    }

    public Boolean nullChecker(Quiz quiz){
        if(quiz.getTitle()== null || quiz.getDescription()== null || quiz.getCreatedAt()== null ||quiz.getStartingTime()== null || quiz.getNotes()== null || quiz.getQuestions()== null){
            return false;
        }
        return true;
    }
}
