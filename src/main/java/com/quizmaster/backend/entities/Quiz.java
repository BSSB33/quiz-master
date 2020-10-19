package com.quizmaster.backend.entities;


import lombok.Data;
import nonapi.io.github.classgraph.json.Id;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Quiz {

    @Id
    private String id;

    private String title;
    private LocalDateTime createdAt;
    private LocalDateTime startingTime;
    private String notes;

    private List<Question> questions;

    public Quiz(String title, LocalDateTime startingTime, String notes, List<Question> questions) {
        this.title = title;
        this.createdAt = LocalDateTime.now();
        this.startingTime = startingTime;
        this.notes = notes;
        this.questions = questions;
    }

    @Override
    public String toString() {
        return "Quiz{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", createdAt=" + createdAt +
                ", startingTime=" + startingTime +
                ", notes='" + notes + '\'' +
                ", questions=" + questions +
                '}';
    }
}
