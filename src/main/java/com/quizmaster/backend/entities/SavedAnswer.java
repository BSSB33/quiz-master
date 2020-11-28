package com.quizmaster.backend.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SavedAnswer implements Comparable<SavedAnswer>{

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



    @Override
    public int compareTo(SavedAnswer o) {
        if (this.questionNumber > o.getQuestionNumber()){
            return 1;
        }else if(this.questionNumber < o.getQuestionNumber()){
            return -1;
        }else if(this.questionNumber == o.getQuestionNumber()){

        }

        return 0;
    }
}
