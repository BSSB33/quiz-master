package com.quizmaster.backend.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class MultipleChoicesModel extends Model {

    private String question;
    private List<String> answers;

    @JsonCreator
    public MultipleChoicesModel(@JsonProperty("question") String question, @JsonProperty("answers") List<String> answers, @JsonProperty("correctAnswers") List<Integer> correctAnswers) {
        super(correctAnswers);
        this.question = question;
        this.answers = answers;
    }
}
