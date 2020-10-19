package com.quizmaster.backend.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MultipleChoicesModel extends Model{

    private String question;
    private List<String> answers;
    private List<Integer> correctAnswers;

}
