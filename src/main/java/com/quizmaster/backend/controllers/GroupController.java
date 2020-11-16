package com.quizmaster.backend.controllers;

import com.quizmaster.backend.entities.*;
import com.quizmaster.backend.repositories.UserMongoRepository;
import com.quizmaster.services.GameIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class GroupController {

    @Autowired
    private UserMongoRepository userMongoRepository;

    ArrayList<QuizGame> activeGames = new ArrayList<>();

    public void createGame() {
        //TODO assigned to: Pascal
        //TODO: every minute a loop should check whether the quiz can be started or not, if the date comes the quizGame should be started -> add it to activeGames array before 5 minutes
        //TODO create socket connection for ID -> like in webSocketConfig
        //QuizGame newGame = new QuizGame(quiz);
        //sendNextQuestion()...
    }

    public void sendNextQuestion(){
        //TODO assigned to: Pascal
        //TODO with all the students joined to the game, at the point of the given timestamp, the game can be started -> this is the point wehere we send the first question to everyone
    }


//    @MessageMapping("/join/{gameId}")
//    @SendTo("/results/joined")
//    public ResponseEntity join(@DestinationVariable String gameId, String nickname) { //If object not string: (GameID gameId)
//        //If key valid, game exists:
//        if () {
//            return ResponseEntity.ok().build();
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }

    @GetMapping("/newid")
    public String test() {
        GameIdGenerator tickets = new GameIdGenerator(6);
        return tickets.nextString();
    }

//    private ResponseEntity generateNewGameId(){
//            GameIdGenerator generator = new GameIdGenerator(6);
//            String gameID = generator.nextString();
//        while (usedCodes.contains(gameID)) {
//            System.err.println("Game Id was already used, generating new one...");
//            gameID = generator.nextString();
//        }
//        if (!usedCodes.contains(gameID)) {
//            return ResponseEntity.ok(newGame);
//        } else {
//            return ResponseEntity.badRequest().body("Game ID generation was not successful. (You should never see this error)");
//        }
//    }
}
