package com.quizmaster.backend.repositories;

import com.quizmaster.backend.entities.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMongoRepository extends MongoRepository<User, String> {
    public User getById(String id);

    public User getByEmail(String email);

    public boolean existsUserByEmail(String email);
}
