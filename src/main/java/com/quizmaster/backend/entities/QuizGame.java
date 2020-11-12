package com.quizmaster.backend.entities;

import lombok.Data;

import java.util.ArrayList;


@Data
public class QuizGame {
    private ArrayList<Quiz> quizzes;
    private ArrayList<User> players;
    private User owner;
    private String gameID;

    public QuizGame(User owner, String gameID) {
        this.quizzes = new ArrayList<>();
        this.players = new ArrayList<>();
        this.owner = owner;
        this.gameID = gameID;
    }

    public void addPlayer(User newUser){
        players.add(newUser);
    }
}
