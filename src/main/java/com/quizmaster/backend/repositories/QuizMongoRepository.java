package com.quizmaster.backend.repositories;

import com.quizmaster.backend.entities.Quiz;
import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface QuizMongoRepository extends MongoRepository<Quiz, String> {
    public Quiz getById(String id);

    @DeleteQuery
    void deleteById(String id);
}
