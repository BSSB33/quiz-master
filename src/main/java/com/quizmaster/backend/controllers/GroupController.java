package com.quizmaster.backend.controllers;

import com.quizmaster.backend.entities.*;
import com.quizmaster.backend.repositories.QuizMongoRepository;
import com.quizmaster.backend.repositories.UserMongoRepository;
import com.quizmaster.services.GameIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
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
            if (Act.getStartingTime().truncatedTo(ChronoUnit.MINUTES).isEqual(fiveMinutesLater)){
                System.out.println("Quiz added " + Act.getId());
                activeGames.add(new QuizGame(Act));
            }
        }

        System.out.println("Length activegames: " + activeGames.size());

        sendNextQuestion();
    }

    public void sendNextQuestion(){
        //TODO assigned to: Pascal
        //TODO with all the students joined to the game, at the point of the given timestamp, the game can be started -> this is the point wehere we send the first question to everyone

        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        List<QuizGame> itemsToRemove = new ArrayList<QuizGame>();

        for (QuizGame Act : activeGames){


            LocalDateTime quizTime = Act.getQuiz().getStartingTime().truncatedTo(ChronoUnit.MINUTES);
            long diff = Duration.between(quizTime, now).toMinutes();
            System.out.println("Difference in Time for active game: " + diff);

            if (diff < 0){ //is about to start

            }else{ //is starting or has already started
                Act.incActQuestion();

                if (Act.isNextQuestion() == false){ //game is over
                    itemsToRemove.add(Act);
                    template.convertAndSend("/results/room/" + Act.getQuiz().getId(), "Quiz ended");
                    //TODO Save Results for Teacher
                    sendResults(Act);

                }else{ //game still has more questions
                    System.out.println("Sending out question");
                    System.out.println("Sending out to room: results/room/" + Act.getQuiz().getId());
                    System.out.println("Content of Questions is: " +  Act.getActQuestion().toString());
                    template.convertAndSend("/results/room/" + Act.getQuiz().getId(), Act.getActQuestion().toString());


                }
            }
        }
        activeGames.removeAll(itemsToRemove);
    }

    private void sendResults(QuizGame act) {
        try {
            Thread.sleep(1000); //wait some time to let sockets start up
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("printing results");
        for (PlayerScore userResult : act.getPlayer()){
            System.out.println("Found finished game and sending results");
            template.convertAndSendToUser(userResult.getSessionID(), "/queue/reply", "Results: " + userResult.getAnswers().toString(), createHeaders(userResult.getSessionID()));
        }
    }

    private MessageHeaders createHeaders(String sessionId) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);
        return headerAccessor.getMessageHeaders();
    }





    @MessageMapping("/join/{gameId}")
    @SendToUser("/queue/reply")
    public String join(@Header("simpSessionId") String sessionId, @DestinationVariable String gameId, String nickname) { //If object not string: (GameID gameId)

        System.out.println("Join: Join Request Received");

        for (QuizGame Act : activeGames){
            if (Act.getQuiz().getId().equals(gameId)){ //quiz found
                PlayerScore userInfo = Act.getPlayer(sessionId);
                if (userInfo != null){ //already joined
                    return "Already joined";
                }else{
                    PlayerScore addUser = new PlayerScore(sessionId, LocalDateTime.now());
                    addUser.setNickname(nickname+sessionId.substring(0,3)); // first 3 elements of sessionID are added to nickname to enable secure distribution of names
                    Act.addPlayer(addUser);
                    return "You were added";
                }
            }
        }
        return "GameID not found";
    }

    @MessageMapping("/answer/{gameId}")
    @SendToUser("/queue/reply")
    public String receiveAnswer(@Header("simpSessionId") String sessionId, @DestinationVariable String gameId, String answerChoice) { //If object not string: (GameID gameId)

        System.out.println("Answer Received");

        for (QuizGame Act : activeGames){
            if (Act.getQuiz().getId().equals(gameId)){ //quiz found
                PlayerScore userInfo = Act.getPlayer(sessionId);
                if (userInfo != null){ //already joined

                    //List<Integer> correctAnswer = Act.getActQuestion().getModel().getCorrectAnswers();
                    //Collections.sort(correctAnswer);
                    //Collections.sort(answerChoice);

                    System.out.println("Comparing given and correct answers");

                    if (answerChoice.equals(Act.getActQuestion().getModel().getCorrectAnswers().get(0).toString())){
                        userInfo.addAnswer(Act.getQuestionNumber(), true);
                        System.out.println("Provided answer is correct");
                    }else{
                        userInfo.addAnswer(Act.getQuestionNumber(), false);
                        System.out.println("Provided answer is incorrect");
                    }
                    return "Thanks for your answer";
                }else{
                    return "You did not join correctly to the game";
                }
            }
        }
        return "GameID not found";
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
