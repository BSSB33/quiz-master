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

        LocalDateTime fiveMinutesLater = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plusMinutes(1);
        System.out.println("############################################## New Iteration");
        System.out.println("Time + 1: " + fiveMinutesLater.toString());

        for (Quiz Act : quizMongoRepository.findAll()){
            //System.out.println("Quiz Title" + Act.getTitle());
            //System.out.println("Quiz starting Time" + Act.getStartingTime().truncatedTo(ChronoUnit.MINUTES));
            //System.out.println("compareTime" + fiveMinutesLater);

            if (Act.getStartingTime().truncatedTo(ChronoUnit.MINUTES).isEqual(fiveMinutesLater)){
                System.out.println("Quiz added " + Act.getId());
                activeGames.add(new QuizGame(Act));
            }
        }

        System.out.println("Length activegames: " + activeGames.size());

    }

    @Scheduled(fixedRate = 60000)
    public void sendNextQuestion(){
        //TODO assigned to: Pascal
        //TODO with all the students joined to the game, at the point of the given timestamp, the game can be started -> this is the point wehere we send the first question to everyone

        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);

        for (QuizGame Act : activeGames){

            LocalDateTime quizTime = Act.getQuiz().getStartingTime().truncatedTo(ChronoUnit.MINUTES);
            long diff = Duration.between(quizTime, now).toMinutes();
            System.out.println("Differnce in Time for active game: " + diff);

            if (diff < 0){ //is about to start

            }else{ //is starting or has already started
                if (diff > Act.getQuiz().getQuestions().size()){ //game is over
                    activeGames.remove(Act);
                    template.convertAndSend("/results/room/" + Act.getQuiz().getId(), "Quiz ended");
                }else{ //game still has more questions
                    System.out.println("Sending out question");
                    System.out.println("Sending out to room: results/room/" + Act.getQuiz().getId());
                    System.out.println("Content of Questions is: " +  Act.getQuiz().getQuestions().get((int) diff).toString());
                    template.convertAndSend("/results/room/" + Act.getQuiz().getId(), Act.getQuiz().getQuestions().get((int) diff).toString());
                }
            }
        }
    }


    @MessageMapping("/join/{gameId}")
    @SendTo("/results/joined")
    public String join(@DestinationVariable String gameId, String nickname) { //If object not string: (GameID gameId)
        //If key valid, game exists:
        if (gameId != null) {
            return "Yup, you joined";
        } else {
            return "Nope Error while joining";
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
