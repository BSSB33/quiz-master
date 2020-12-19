package com.quizmaster.backend.entities;


import lombok.Data;
import org.springframework.data.annotation.Id;

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

    public void setCreatedAt(LocalDateTime creatingTime) {
        this.createdAt = creatingTime;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public LocalDateTime getStartingTime() {
        return this.startingTime;
    }

    public String getId() {
        return this.id;
    }

    public String getOwnerId() {
        return this.ownerId;
    }

    public void setOwnerId(String id) {
        this.ownerId = id;
    }

    public List<Question> getQuestions() {
        return this.questions;
    }
}
