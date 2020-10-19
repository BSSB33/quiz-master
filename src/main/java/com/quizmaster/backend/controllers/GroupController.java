package com.quizmaster.backend.controllers;

import com.quizmaster.backend.entities.*;
import com.quizmaster.services.GameIdGenerator;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@RestController
public class GroupController {

    Set<String> usedCodes = new HashSet<>();
    ArrayList<QuizGame> activeGames = new ArrayList<>();

    @MessageMapping("/create")
    @SendTo("/results/joinok")
    public Response createGame(String teacherUserName) { //This might be changed to User object
        GameIdGenerator generator = new GameIdGenerator(6);
        String gameID = generator.nextString();
        activeGames.add(new QuizGame(new User(teacherUserName))); //TODO Get User from DB
        if (!usedCodes.contains(gameID)) {
            usedCodes.add(gameID);
            return new Ok(200, 0, gameID);
        } else {
            while (usedCodes.contains(gameID)) {
                gameID = generator.nextString();
            }
            if (!usedCodes.contains(gameID)) {
                return new Ok(200, 0, gameID);
            } else return new Fail(000); //TODO replace 000 to preoper error code
        }
    }

    @MessageMapping("/join") //if a message is sent to the /hello destination, the greeting() method is called.
    @SendTo("/results/joinok")
    //The payload of the message is bound to a HelloMessage object, which is passed into greeting().
    public Response join(GameID gameID) {
        //If key valid, game exists:
        if (usedCodes.contains(gameID.getID())) {
            System.out.println("Successfully connected to game ID:" + gameID.getID());
            return new Ok(200, 100000, gameID.getID()); //ID can be removed from there
        } else {
            return new Fail(404);
        }
    }

    @GetMapping("/newid")
    public String test() {
        GameIdGenerator tickets = new GameIdGenerator(6);
        return tickets.nextString();
    }
}
