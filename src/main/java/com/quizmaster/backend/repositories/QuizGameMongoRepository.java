package com.quizmaster.backend.repositories;

import com.quizmaster.backend.entities.QuizGame;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface QuizGameMongoRepository extends MongoRepository<QuizGame, String> {
    public QuizGame getById(String id);

    public boolean exists(QuizGame item);

}
