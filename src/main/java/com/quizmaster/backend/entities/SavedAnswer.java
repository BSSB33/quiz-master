package com.quizmaster.backend.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SavedAnswer implements Comparable<SavedAnswer>{

    int questionNumber;
    Answer isCorrect;
    List<Integer> givenAnswer;

    @JsonCreator
    public SavedAnswer(@JsonProperty("questionNumber") int questionNumber, @JsonProperty("isCorrect") Answer isCorrect) {
        this.questionNumber = questionNumber;
        this.isCorrect = isCorrect;
        this.givenAnswer = new ArrayList<>();
    }

    public void setCorrect(Answer stateAnswer) {
        this.isCorrect = stateAnswer;
    }

    public void setGivenAnswer(List<Integer> answer){
        this.givenAnswer = answer;
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
