package com.quizmaster.repositories;

import com.quizmaster.entities.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserMongoRepository extends MongoRepository<User, Integer> {
    User findByName(String name);
}
