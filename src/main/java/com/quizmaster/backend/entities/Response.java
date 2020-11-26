package com.quizmaster.backend.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Response {
    int code;
    int time;
    String gameID;
}
