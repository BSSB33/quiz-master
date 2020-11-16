package com.quizmaster.backend.controllers;

import com.quizmaster.backend.entities.*;
import com.quizmaster.backend.repositories.QuizMongoRepository;
import com.quizmaster.backend.repositories.UserMongoRepository;
import com.quizmaster.services.GameIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.Collection;

@RestController
@CrossOrigin
@EnableScheduling
public class GroupController {

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private QuizMongoRepository quizMongoRepository;

    @Autowired
    private UserMongoRepository userMongoRepository;

    ArrayList<QuizGame> activeGames = new ArrayList<>();

    @Scheduled(fixedRate = 60000)
    public void createGame() {
        //TODO assigned to: Pascal
        //TODO: every minute a loop should check whether the quiz can be started or not, if the date comes the quizGame should be started -> add it to activeGames array before 5 minutes
        //TODO create socket connection for ID -> like in webSocketConfig

        LocalDateTime fiveMinutesLater = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plusMinutes(5);
        System.out.println("Time + 5: " + fiveMinutesLater.toString());

        for (Quiz Act : quizMongoRepository.findAll()){
            if (Act.getStartingTime().truncatedTo(ChronoUnit.MINUTES).isEqual(fiveMinutesLater)){
                activeGames.add(new QuizGame(Act));
            }
        }

    }

    @Scheduled(fixedRate = 60000)
    public void sendNextQuestion(){
        //TODO assigned to: Pascal
        //TODO with all the students joined to the game, at the point of the given timestamp, the game can be started -> this is the point wehere we send the first question to everyone

        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plusMinutes(5);

        for (QuizGame Act : activeGames){

            LocalDateTime quizTime = Act.getQuiz().getStartingTime().truncatedTo(ChronoUnit.MINUTES);
            long diff = Duration.between(quizTime, now).toMinutes();
            if (diff < 0){ //is about to start

            }else{ //is starting or has already started
                if (diff > Act.getQuiz().getQuestions().size()){ //game is over
                    activeGames.remove(Act);
                    template.convertAndSend("game/room/" + Act.getQuiz().getid(), "Quiz ended");
                }else{ //game still has more questions
                    template.convertAndSend("game/room/" + Act.getQuiz().getid(), Act.getQuiz().getQuestions().get((int) diff));
                }
            }
        }
    }


    @MessageMapping("/join/{gameId}")
    @SendTo("/results/joined")
    public Ok join(@DestinationVariable String gameId, String nickname) { //If object not string: (GameID gameId)
        //If key valid, game exists:
        if (gameId != null) {
            return new Ok(1, 1, "1");
        } else {
            return null;
        }
    }

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
