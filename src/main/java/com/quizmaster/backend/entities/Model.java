package com.quizmaster.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class Model {
    @JsonIgnore
    private List<Integer> correctAnswers;
}
