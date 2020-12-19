package com.quizmaster.backend.controllers;

import com.quizmaster.backend.entities.User;
import com.quizmaster.backend.repositories.UserMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserMongoRepository userMongoRepository;

    @GetMapping("")
    public ResponseEntity getAll() {
        System.out.println(userMongoRepository.findAll());
        return ResponseEntity.ok(userMongoRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity getById(@PathVariable String id) {
        if (userMongoRepository.existsById(id)) {
            return ResponseEntity.ok(userMongoRepository.getById(id));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("")
    public ResponseEntity postById(@RequestBody User userToSave) {
        if (userToSave.getId() != null && userMongoRepository.existsById(userToSave.getId())) { // ERROR: getId should not be called before save!
            return ResponseEntity.badRequest().body("ID Taken!");
        }
        if (userToSave.getGoogleId() == null || userToSave.getEmail() == null)
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok(userMongoRepository.save(userToSave));
    }

    @PutMapping("/{id}")
    public ResponseEntity putById(@PathVariable String id, @RequestBody User userToSave) {

        if (userToSave.getGoogleId() == null || userToSave.getEmail() == null)
            return ResponseEntity.noContent().build();

        if (userMongoRepository.existsById(id)) {
            userToSave.setId(id);
            return ResponseEntity.ok(userMongoRepository.save(userToSave));
        }
        return ResponseEntity.notFound().build();

    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteById(@PathVariable String id) {
        if (userMongoRepository.existsById(id)) {
            userMongoRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }
}
