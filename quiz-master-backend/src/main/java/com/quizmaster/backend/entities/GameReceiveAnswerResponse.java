package com.quizmaster.backend.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GameReceiveAnswerResponse {
    String code;
    boolean correct;

    @JsonCreator
    public GameReceiveAnswerResponse(@JsonProperty("code") String code, @JsonProperty("correct") boolean correct) {
        this.code = code;
        this.correct = correct;
    }

}
