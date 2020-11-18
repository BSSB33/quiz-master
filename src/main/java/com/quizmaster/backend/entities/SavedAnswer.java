package com.quizmaster.backend.entities;

import lombok.Data;

@Data
public class SavedAnswer {

    int questionNumber;
    boolean isCorrect;

    public SavedAnswer(int questionNumber, boolean isCorrect)
    {
        this.questionNumber = questionNumber;
        this.isCorrect = isCorrect;
    }

}
