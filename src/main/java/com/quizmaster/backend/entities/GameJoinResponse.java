package com.quizmaster.backend.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GameJoinResponse {
    String code;
    boolean correct;
    LocalDateTime startingTime;

    @JsonCreator
    public GameJoinResponse(@JsonProperty("code") String code, @JsonProperty("correct") boolean correct, LocalDateTime startingTime){
        this.code = code;
        this.correct = correct;
        this.startingTime = startingTime;
    }

}
