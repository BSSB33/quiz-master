package com.quizmaster.backend.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.quizmaster.backend.services.QuestionDeserializer;
import lombok.Data;

@JsonDeserialize(using = QuestionDeserializer.class)
@Data
public class Question {

    private String type;
    private Model model;

    @JsonCreator
    public Question(@JsonProperty("type") String type, @JsonProperty("model") Model model) {
        this.type = type;
        this.model = model;
    }


    public Model getModel() {
        return model;
    }
}
