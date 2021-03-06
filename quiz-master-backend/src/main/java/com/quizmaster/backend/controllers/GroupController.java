package com.quizmaster.backend.controllers;

import com.quizmaster.backend.entities.*;
import com.quizmaster.backend.repositories.QuizGameMongoRepository;
import com.quizmaster.backend.repositories.QuizMongoRepository;
import com.quizmaster.backend.repositories.UserMongoRepository;
import com.quizmaster.backend.services.GameIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@CrossOrigin
@EnableScheduling
public class GroupController {

    private final long TIMEWINDOW = 300; // in Seconds
    private final long SCHEDULERATE = 1; // in Seconds
    private final long QUESTIONTIME = 20; // in Seconds

    public GroupController() {
        super();
    }

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private QuizMongoRepository quizMongoRepository;

    @Autowired
    private QuizGameMongoRepository quizGameMongoRepository;

    @Autowired
    private UserMongoRepository userMongoRepository;

    ArrayList<QuizGame> activeGames = new ArrayList<>();

    @Scheduled(fixedRate = SCHEDULERATE * 1000)
    public void createGame() {
        for (Quiz act : quizMongoRepository.findAll()) {

            if (act.getStartingTime().isAfter(LocalDateTime.now()) && act.getStartingTime().minusSeconds(TIMEWINDOW).isBefore(LocalDateTime.now())) {
                boolean containsId = false;
                for (QuizGame activeGame : activeGames) {
                    if (activeGame.getQuiz().getId().equals(act.getId())) {
                        containsId = true;
                        break;
                    }
                }
                if (!containsId) activeGames.add(new QuizGame(act));
            }
        }

        System.out.println("Length activegames: " + activeGames.size());
        sendNextQuestion();
    }

    public void sendNextQuestion() {

        List<QuizGame> itemsToRemove = new ArrayList<QuizGame>();

        for (QuizGame act : activeGames) {
            if (LocalDateTime.now().isBefore(act.getQuiz().getStartingTime())) { //is about to start but not ready

            } else { //is starting or has already started

                if (act.getLastQuestionSend().plusSeconds(QUESTIONTIME).isBefore(LocalDateTime.now())) {
                    act.setLastQuestionSend(LocalDateTime.now());
                    act.incActQuestion();
                    if (!act.isNextQuestion()) { //game is over
                        itemsToRemove.add(act);
                        QuizEndedResponse toSend = new QuizEndedResponse("Quiz ended");
                        template.convertAndSend("/results/room/" + act.getQuiz().getId(), toSend);
                        System.out.println("sent out--------------------------------------");
                        quizGameMongoRepository.save(act); // Save Results for Teacher
                        sendResults(act);
                    } else {
                        System.out.println("Sending out question");
                        System.out.println("Sending out to room: results/room/" + act.getQuiz().getId());
                        System.out.println("Content of Questions is: " + act.getActQuestion().toString());

                        List<Integer> temp = act.getActQuestion().getModel().getCorrectAnswers();
                        act.getActQuestion().getModel().setCorrectAnswers(null);
                        template.convertAndSend("/results/room/" + act.getQuiz().getId(), act.getActQuestion());
                        act.getActQuestion().getModel().setCorrectAnswers(temp);
                    }
                }
            }
        }
        activeGames.removeAll(itemsToRemove);
    }

    private void sendResults(QuizGame act) {
        try {
            Thread.sleep(100); //wait some time to let clients receive Quiz ended first
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("printing results");
        for (PlayerScore userResult : act.getPlayer()) {
            userResult.fillUnanswered(act.getQuestionNumber());

            System.out.println("Found finished game and sending results");
            ResultResponse sendToPlayer = new ResultResponse(userResult, act.getAllQuestions());
            template.convertAndSendToUser(userResult.getSessionID(), "/queue/reply", sendToPlayer, createHeaders(userResult.getSessionID()));
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
    public GameJoinResponse join(@Header("simpSessionId") String sessionId, @DestinationVariable String gameId, String nickname) {

        System.out.println("Join: Join Request Received");


        for (QuizGame act : activeGames) {
            if (act.getQuiz().getId().equals(gameId)) { //quiz found
                PlayerScore userInfo = act.getPlayer(sessionId);
                if (userInfo != null) { //already joined
                    return new GameJoinResponse("Already joined", false, act.getQuiz().getStartingTime(), act.getQuiz().getTitle(), act.getQuiz().getDescription());
                } else {
                    if (act.isNicknameAlreadyUsed(nickname)) { // If nickname already given out to someone
                        return new GameJoinResponse("Nickname already given out", false, act.getQuiz().getStartingTime(), act.getQuiz().getTitle(), act.getQuiz().getDescription());
                    }
                    PlayerScore addUser = new PlayerScore(sessionId, LocalDateTime.now());
                    addUser.setNickname(nickname); // first 3 elements of sessionID are added to nickname to enable secure distribution of names
                    act.addPlayer(addUser);
                    return new GameJoinResponse("You were added", true, act.getQuiz().getStartingTime(), act.getQuiz().getTitle(), act.getQuiz().getDescription());
                }
            }
        }

        for (Quiz act : quizMongoRepository.findAll()) {
            if (act.getId().equals(gameId)) {
                if (act.getStartingTime().isBefore(LocalDateTime.now())) {
                    return new GameJoinResponse("Quiz already ended", false, act.getStartingTime(), act.getTitle(), act.getDescription());
                }
                if (act.getStartingTime().isAfter(LocalDateTime.now())) {
                    return new GameJoinResponse("Quiz will start", false, act.getStartingTime(), act.getTitle(), act.getDescription());
                }
            }
        }

        return new GameJoinResponse("Quiz not found", false, null, null, null);
    }

    @MessageMapping("/answer/{gameId}")
    @SendToUser("/queue/reply")
    public GameReceiveAnswerResponse receiveAnswer(@Header("simpSessionId") String sessionId, @DestinationVariable String gameId, List<Integer> answerChoice) {

        System.out.println("Answer Received");

        for (QuizGame Act : activeGames) {
            if (Act.getQuiz().getId().equals(gameId)) { //quiz found
                PlayerScore userInfo = Act.getPlayer(sessionId);
                if (userInfo != null) { //already joined

                    List<Integer> correctAnswer = Act.getActQuestion().getModel().getCorrectAnswers();
                    Collections.sort(correctAnswer);
                    Collections.sort(answerChoice);

                    System.out.println("Comparing given and correct answers");

                    if (answerChoice.equals(Act.getActQuestion().getModel().getCorrectAnswers())) {
                        userInfo.addAnswer(Act.getQuestionNumber(), Answer.CORRECT, answerChoice);
                        System.out.println("Provided answer is correct");
                    } else {
                        userInfo.addAnswer(Act.getQuestionNumber(), Answer.INCORRECT, answerChoice);
                        System.out.println("Provided answer is incorrect");
                    }
                    return new GameReceiveAnswerResponse("Thanks for your answer", true);
                } else {
                    return new GameReceiveAnswerResponse("You did not join correctly to the game", false);
                }
            }
        }
        return new GameReceiveAnswerResponse("GameID not found", false);
    }

    @GetMapping("/newid")
    public String test() {
        GameIdGenerator tickets = new GameIdGenerator(6);
        return tickets.nextString();
    }

}
