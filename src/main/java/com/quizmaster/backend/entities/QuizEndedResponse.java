package com.quizmaster.backend.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class QuizEndedResponse {
    String message;

    @JsonCreator
    public QuizEndedResponse(@JsonProperty("message") String message){
        this.message = message;
    }

}
