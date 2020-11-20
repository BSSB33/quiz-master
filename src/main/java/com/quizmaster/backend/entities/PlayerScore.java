package com.quizmaster.backend.entities;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.quizmaster.backend.services.QuestionDeserializer;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;


@JsonDeserialize(using = QuestionDeserializer.class)
@Data
public class PlayerScore {
    String nickname;
    LocalDateTime connectAt;
    ArrayList<SavedAnswer> answers;
    String sessionID;

    public PlayerScore(String ID, LocalDateTime created) {
        this.sessionID = ID;
        this.connectAt = created;
        this.answers = new ArrayList<SavedAnswer>();
        this.nickname = ID;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void addAnswer(int questionNumber, boolean isCorrect) {
        for (SavedAnswer temp : this.answers) {
            if (temp.getQuestionNumber() == questionNumber) {
                temp.setCorrect(isCorrect);
                return;
            }
        }
        this.answers.add(new SavedAnswer(questionNumber, isCorrect));
    }

}
