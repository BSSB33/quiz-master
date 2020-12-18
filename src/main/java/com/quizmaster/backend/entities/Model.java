package com.quizmaster.backend.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class Model {
    private List<Integer> correctAnswers;
}
