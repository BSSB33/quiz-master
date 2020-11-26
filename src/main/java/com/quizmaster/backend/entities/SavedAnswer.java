package com.quizmaster.backend.entities;

import lombok.Data;

@Data
public class SavedAnswer {

    int questionNumber;
    Answer isCorrect;

    public SavedAnswer(int questionNumber, Answer isCorrect) {
        this.questionNumber = questionNumber;
        this.isCorrect = isCorrect;
    }


    public void setCorrect(Answer stateAnswer) {
        this.isCorrect = stateAnswer;
    }


}
