package com.quizmaster.backend.repositories;

import com.quizmaster.backend.entities.Quiz;
import com.quizmaster.backend.entities.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface QuizMongoRepository extends MongoRepository<Quiz, String> {
    public Quiz getById(String id);


}
