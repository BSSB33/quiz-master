package com.quizmaster.backend.controllers;

import com.quizmaster.backend.entities.*;
import com.quizmaster.backend.repositories.UserMongoRepository;
import com.quizmaster.services.GameIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@RestController
public class GroupController {

    @Autowired
    private UserMongoRepository userMongoRepository;

    Set<String> usedCodes = new HashSet<>();
    ArrayList<QuizGame> activeGames = new ArrayList<>();

    @MessageMapping("/create")
    @SendTo("/results/created")
    public ResponseEntity createGame(String teacherUserName) { //This might be changed to User object
        GameIdGenerator generator = new GameIdGenerator(6);
        String gameID = generator.nextString();
        QuizGame newGame = createQuizGame(teacherUserName);
        if (!usedCodes.contains(gameID)) {
            usedCodes.add(gameID);
            return ResponseEntity.ok(newGame); //TODO Add date of start
        } else {
            while (usedCodes.contains(gameID)) {
                System.err.println("Game Id was already used, generating new one...");
                gameID = generator.nextString();
            }
            if (!usedCodes.contains(gameID)) {
                return ResponseEntity.ok(newGame);
            } else {
                return ResponseEntity.badRequest().body("Game ID generation was not successful. (You should never see this error)");
            }
        }
    }

    private QuizGame createQuizGame(String teacherUserName) {
        QuizGame newGame = new QuizGame(userMongoRepository.getByName(teacherUserName));
        activeGames.add(newGame);
        //TODO create socket with id
        return newGame;
    }

    @MessageMapping("/join/{gameId}") //if a message is sent to the /hello destination, the greeting() method is called.
    @SendTo("/results/joined")
    public ResponseEntity join(GameID gameID) {
        //If key valid, game exists:
        if (usedCodes.contains(gameID.getID())) {
            System.out.println("Successfully connected to game ID:" + gameID.getID());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/newid")
    public String test() {
        GameIdGenerator tickets = new GameIdGenerator(6);
        return tickets.nextString();
    }
}
