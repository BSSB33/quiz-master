package com.quizmaster.backend.entities;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.quizmaster.backend.services.QuestionDeserializer;
import com.quizmaster.backend.services.QuestionSerializer;
import lombok.Data;

import java.util.List;

@JsonDeserialize(using = QuestionDeserializer.class)
@JsonSerialize(using = QuestionSerializer.class)
@Data
public class Question {

    private String type;
    private Model model;

    public Question(String type, Model model) {
        this.type = type;
        this.model = model;
    }
}
