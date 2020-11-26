package com.quizmaster.backend.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SavedAnswer {

    int questionNumber;
    Answer isCorrect;
    @JsonCreator
    public SavedAnswer(@JsonProperty("questionNumber") int questionNumber, @JsonProperty("isCorrect") Answer isCorrect) {
        this.questionNumber = questionNumber;
        this.isCorrect = isCorrect;
    }


    public void setCorrect(Answer stateAnswer) {
        this.isCorrect = stateAnswer;
    }


}
