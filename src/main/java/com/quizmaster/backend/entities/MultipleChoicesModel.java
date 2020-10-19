package com.quizmaster.backend.entities;

import lombok.Data;

import java.util.List;

@Data
public class MultipleChoicesModel extends Model{

    private String question;
    private List<String> answers;
    private List<Integer> correctAnswers;
    private String type;

    public MultipleChoicesModel(String question, List<String> answers, List<Integer> correctAnswers) {
        this.question = question;
        this.answers = answers;
        this.correctAnswers = correctAnswers;
    }
}
