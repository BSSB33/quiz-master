package com.quizmaster.backend.entities;

import org.springframework.data.annotation.Id;

public class User {
    @Id
    private String id;

    private String name;

    public User(String name) {
        this.name = name;
    }
}
