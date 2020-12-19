package com.quizmaster.backend.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GameJoinResponse {
    String code;
    boolean correct;
    LocalDateTime startingTime;
    String quizTitle;
    String quizDescription;

    @JsonCreator
    public GameJoinResponse(@JsonProperty("code") String code, @JsonProperty("correct") boolean correct, @JsonProperty("startingTime") LocalDateTime startingTime, @JsonProperty("quizTitle") String quizTitle, @JsonProperty("quizDescription") String quizDescription) {
        this.code = code;
        this.correct = correct;
        this.startingTime = startingTime;
        this.quizTitle = quizTitle;
        this.quizDescription = quizDescription;
    }

}
