package com.quizmaster.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Response {
    private int code;
    private int time; //Time till the game starts
    private String gameID; //The game id the user joined successfully
}
