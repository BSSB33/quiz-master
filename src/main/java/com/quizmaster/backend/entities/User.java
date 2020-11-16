package com.quizmaster.backend.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class User {

    @Id
    private String id;
    private String email;
    private String googleId;

    public User(String email, String googleId) {
        this.email = email;
        this.googleId = googleId;
    }
}
