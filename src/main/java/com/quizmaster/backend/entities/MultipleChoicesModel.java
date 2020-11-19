package com.quizmaster.backend.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class MultipleChoicesModel extends Model{

    private String question;
    private List<String> answers;
    private List<Integer> correctAnswers;

}
