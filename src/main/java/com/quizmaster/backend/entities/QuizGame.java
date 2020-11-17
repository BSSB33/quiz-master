package com.quizmaster.backend.entities;

import lombok.Data;

import java.util.ArrayList;


@Data
public class QuizGame {
    private Quiz quiz;
    private ArrayList<PlayerScore> Player;

    public QuizGame(Quiz quiz) {
        this.quiz = quiz;
        this.Player = new ArrayList<PlayerScore>();
    }

    public void addPlayer(PlayerScore newUser){ Player.add(newUser); }

    public ArrayList<PlayerScore> getPlayer(){ return this.Player; }


    public PlayerScore getPlayer(String sessionID){

        for (PlayerScore user : this.Player){
            if (user.getSessionID().equals(sessionID)){
                return user;
            }
        }

        return null;
    }

    public Quiz getQuiz() { return this.quiz;}
}
