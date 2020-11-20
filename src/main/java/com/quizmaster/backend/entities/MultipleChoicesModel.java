package com.quizmaster.backend.entities;

import lombok.Data;

import java.util.List;

@Data
public class MultipleChoicesModel extends Model {

    private String question;
    private List<String> answers;

    public MultipleChoicesModel(String question, List<String> answers, List<Integer> correctAnswers) {
        super(correctAnswers);
        this.question = question;
        this.answers = answers;
    }


}
