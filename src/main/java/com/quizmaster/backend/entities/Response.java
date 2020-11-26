package com.quizmaster.backend.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class Response {
    int code;
    LocalDateTime time;
    String gameID;
}
