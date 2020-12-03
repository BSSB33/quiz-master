package com.quizmaster.backend.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.quizmaster.backend.services.QuestionDeserializer;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
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
        System.out.println("amount of questions " + questionAmount);

        List<Integer> unanswered =new ArrayList<Integer>();
        for (int i = 0; i<questionAmount; i++){
            unanswered.add(i);
        }

        System.out.println("List original");
        System.out.println(unanswered.toString());

        for (SavedAnswer temp : this.answers) {
            System.out.println("remove " + temp.getQuestionNumber());
            unanswered.remove( (Integer) temp.getQuestionNumber());
            System.out.println("unanswered list: " + unanswered.toString());
            System.out.println("act List: " + this.answers.toString());
        }

        for (Integer notIncluded : unanswered){
            addAnswer(notIncluded, Answer.NOTANSWERED);
        }
        Collections.sort(this.answers);
    }
}
