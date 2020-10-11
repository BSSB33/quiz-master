package com.quizmaster.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Quiz {
    private String question;
    private String answer1;
    private String answer2;
    private String answer3;
    private String answer4;
    private ArrayList<String> correctAnswers;
}
