package com.quizmaster.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class QuizGame {

    @Id
    private String id;
    private Quiz quiz;
    private ArrayList<PlayerScore> player;
    private int actQuestion;
    private LocalDateTime lastQuestionSend;

    public QuizGame(Quiz quiz) {
        this.quiz = quiz;
        this.player = new ArrayList<>();
        this.actQuestion = -1;
        this.lastQuestionSend = LocalDateTime.now().minusDays(1);
    }

    public void addPlayer(PlayerScore newUser) {
        player.add(newUser);
    }

    public ArrayList<PlayerScore> getPlayer() {
        return this.player;
    }

    public boolean isNicknameAlreadyUsed(String nickname) {
        for (PlayerScore user : this.player) {
            if (user.getNickname().equalsIgnoreCase(nickname)) {
                return true;
            }
        }
        return false;
    }

    public PlayerScore getPlayer(String sessionID) {
        for (PlayerScore user : this.player) {
            if (user.getSessionID().equals(sessionID)) {
                return user;
            }
        }
        return null;
    }

    @JsonIgnore
    public Question getActualQuestion() {
        return quiz.getQuestions().get(actQuestion);
    }

    public int getActQuestion() {
        return this.actQuestion;
    }

    public void incActQuestion() {
        this.actQuestion++;
    }

    public boolean isNextQuestion() {
        if (this.actQuestion < quiz.getQuestions().size()) {
            return true;
        } else {
            return false;
        }
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public List<Question> getAllQuestions() {
        return this.quiz.getQuestions();
    }


}
