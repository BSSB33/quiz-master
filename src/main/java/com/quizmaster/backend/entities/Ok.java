package com.quizmaster.backend.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class Ok extends Response {
    @JsonCreator
    public Ok(@JsonProperty("code") int code, @JsonProperty("time") LocalDateTime time, @JsonProperty("gameID") String gameID) {
        super(code, time, gameID);
    }
}
