package com.quizmaster.backend.entities;

public class Ok extends Response{
    public Ok(int code, int time, String gameID) {
        super(code, time, gameID);
    }
}
