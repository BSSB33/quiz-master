package com.quizmaster.backend.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.quizmaster.backend.services.QuestionDeserializer;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@JsonDeserialize(using = QuestionDeserializer.class)
@Data
public class PlayerScore {
    @Id
    String nickname;
    LocalDateTime connectAt;
    ArrayList<SavedAnswer> answers;
    String sessionID;

    @JsonCreator
    public PlayerScore(@JsonProperty("nickname") String nickname, @JsonProperty("created") LocalDateTime connectAt) {
        this.sessionID = nickname;
        this.connectAt = connectAt;
        this.answers = new ArrayList<>();
        this.nickname = nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void addAnswer(int questionNumber, Answer stateAnswer, List<Integer> givenAnswer) {
        for (SavedAnswer temp : this.answers) {
            if (temp.getQuestionNumber() == questionNumber) {
                temp.setGivenAnswer(givenAnswer);
                temp.setCorrect(stateAnswer);
                return;
            }
        }
        this.answers.add(new SavedAnswer(questionNumber, stateAnswer, givenAnswer));
    }

    public void fillUnanswered(int questionAmount) {
        System.out.println("amount of questions " + questionAmount);

        List<Integer> unanswered = new ArrayList<Integer>();
        for (int i = 0; i < questionAmount; i++) {
            unanswered.add(i);
        }

        System.out.println("List original");
        System.out.println(unanswered.toString());

        for (SavedAnswer temp : this.answers) {
            System.out.println("remove " + temp.getQuestionNumber());
            unanswered.remove((Integer) temp.getQuestionNumber());
            System.out.println("unanswered list: " + unanswered.toString());
            System.out.println("act List: " + this.answers.toString());
        }

        for (Integer notIncluded : unanswered) {
            addAnswer(notIncluded, Answer.NOTANSWERED, List.of());
        }
        Collections.sort(this.answers);
    }
}
