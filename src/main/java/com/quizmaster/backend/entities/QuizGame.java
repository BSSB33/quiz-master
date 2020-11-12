package com.quizmaster.backend.entities;

import lombok.Data;

import java.util.ArrayList;


@Data
public class QuizGame {
    private ArrayList<Quiz> quizzes = new ArrayList<>();
    private ArrayList<User> players = new ArrayList<>();
    private User owner;

    public QuizGame(User owner) {
        this.quizzes = new ArrayList<>(); //TODO Load from DB
        this.players = new ArrayList<>(); //TODO Load from DB
        this.owner = owner;
    }
}
