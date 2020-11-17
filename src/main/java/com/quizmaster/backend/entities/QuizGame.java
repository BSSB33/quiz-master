package com.quizmaster.backend.entities;

import lombok.Data;

import java.util.ArrayList;


@Data
public class QuizGame {
    private Quiz quiz;
    private ArrayList<String> nicknames;
    private boolean isStarted;
    //private String gameID;

    public QuizGame(Quiz quiz) {
        this.quiz = quiz;
        this.nicknames = new ArrayList<>();
    }

    public void addPlayer(String newNickname){
        nicknames.add(newNickname);
    }

    public Quiz getQuiz() { return this.quiz;}
}
