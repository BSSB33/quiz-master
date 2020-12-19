package com.quizmaster.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Data
public class QuizGame {

    @Id
    private String id;
    private Quiz quiz;
    private ArrayList<PlayerScore> Player;
    private int actQuestion;
    private LocalDateTime lastQuestionSend;

    public QuizGame(Quiz quiz) {
        this.quiz = quiz;
        this.Player = new ArrayList<PlayerScore>();
        this.actQuestion = -1;
        this.lastQuestionSend = LocalDateTime.now().minusDays(1);
    }

    public void addPlayer(PlayerScore newUser) {
        Player.add(newUser);
    }

    public ArrayList<PlayerScore> getPlayer() {
        return this.Player;
    }

    public boolean isNicknameAlreadyUsed(String nickname) {
        for (PlayerScore user : this.Player) {
            if (user.getNickname().toLowerCase().equals(nickname.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public PlayerScore getPlayer(String sessionID) {
        for (PlayerScore user : this.Player) {
            if (user.getSessionID().equals(sessionID)) {
                return user;
            }
        }
        return null;
    }

    @JsonIgnore
    public Question getActQuestion() {
        return this.quiz.getQuestions().get(this.actQuestion);
    }

    public int getQuestionNumber() {
        return this.actQuestion;
    }

    public void incActQuestion() {
        this.actQuestion++;
    }

    public boolean isNextQuestion() {
        if (this.actQuestion < this.quiz.getQuestions().size()) {
            return true;
        } else {
            return false;
        }
    }

    public Quiz getQuiz() {
        return this.quiz;
    }

    public List<Question> getAllQuestions() {
        return this.quiz.getQuestions();
    }


}
