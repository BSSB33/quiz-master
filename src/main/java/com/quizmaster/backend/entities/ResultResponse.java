package com.quizmaster.backend.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ResultResponse {
    PlayerScore individualResult;
    List<Question> publicQuestions;

    @JsonCreator
    public ResultResponse(@JsonProperty("score") PlayerScore score, @JsonProperty("questions") List<Question> questions){
        this.individualResult = score;
        this.publicQuestions = questions;
    }

}
