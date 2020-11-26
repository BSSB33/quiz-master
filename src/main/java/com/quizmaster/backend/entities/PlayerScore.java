package com.quizmaster.backend.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.quizmaster.backend.services.QuestionDeserializer;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@JsonDeserialize(using = QuestionDeserializer.class)
@Data
public class PlayerScore {
    String nickname;
    LocalDateTime connectAt;
    ArrayList<SavedAnswer> answers;
    String sessionID;

    @JsonCreator
    public PlayerScore(@JsonProperty("ID") String ID, @JsonProperty("created") LocalDateTime created) {
        this.sessionID = ID;
        this.connectAt = created;
        this.answers = new ArrayList<SavedAnswer>();
        this.nickname = ID;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void addAnswer(int questionNumber, Answer stateAnswer) {
        for (SavedAnswer temp : this.answers) {
            if (temp.getQuestionNumber() == questionNumber) {
                temp.setCorrect(stateAnswer);
                return;
            }
        }
        this.answers.add(new SavedAnswer(questionNumber, stateAnswer));
    }

    public void fillUnanswered(int questionAmount){
        List<Integer> unanswered =new ArrayList<Integer>();
        for (int i = 0; i<questionAmount; i++){
            unanswered.add(i);
        }
        for (SavedAnswer temp : this.answers) {
            unanswered.remove(temp.getQuestionNumber());
        }

        for (Integer notIncluded : unanswered){
            addAnswer(notIncluded, Answer.NOTANSWERED);
        }
    }
}
