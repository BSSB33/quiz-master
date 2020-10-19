package com.quizmaster.backend.entities;

import java.util.List;

public class Question {

    private String type;
    private Model model;

    public Question(String type, Model model) {
        this.type = type;

        if(type.equals("multiple") && model instanceof MultipleChoicesModel) this.model = model;
        if(type.equals("image") && model instanceof ImageQuestionModell) this.model = model;

    }
}
