package com.quizmaster.backend.entities;


import lombok.Data;
import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Quiz {

    @Id
    private String id;

    private String title;
    private String description;

    private LocalDateTime createdAt;
    private LocalDateTime startingTime;
    private String notes;
    private String ownerId;
    private List<Question> questions;

    public Quiz(String title, String description, LocalDateTime startingTime, String notes, List<Question> questions) {
        this.title = title;
        this.description = description;
        this.createdAt = LocalDateTime.now();
        this.startingTime = startingTime;
        this.notes = notes;
        this.questions = questions;
    }
}
