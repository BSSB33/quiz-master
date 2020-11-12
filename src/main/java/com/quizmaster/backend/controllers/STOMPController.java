package com.quizmaster.backend.controllers;


import com.quizmaster.backend.entities.MessageObject;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class STOMPController {


    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public String greeting(String message) throws Exception {
        System.out.println("STOMPController gretting active");
        Thread.sleep(1000); // simulated delay
        return message;
    }

}