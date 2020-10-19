package com.quizmaster.backend.entities;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Model {

    private String question;
    private List<String> answers;
    private List<Integer> correctAnswers;

    public Model(String question, List<String> answers, List<Integer> correctAnswers) {
        this.question = question;
        this.answers = answers;
        this.correctAnswers = correctAnswers;
    }
}
