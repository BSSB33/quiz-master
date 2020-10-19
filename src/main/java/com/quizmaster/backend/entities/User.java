package com.quizmaster.backend.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class User {

    @Id
    private String id;

    private String name;

    public User(String name) {
        this.name = name;
    }
}
