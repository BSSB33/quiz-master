package com.quizmaster.backend;

import com.quizmaster.backend.entities.*;
import com.quizmaster.backend.repositories.QuizMongoRepository;
import com.quizmaster.backend.repositories.UserMongoRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@SpringBootApplication
public class BackendApplication<data_type> implements CommandLineRunner {


    public Boolean disabledSecurity = false;
    @Autowired
    private UserMongoRepository userMongoRepository;

    @Autowired
    private QuizMongoRepository quizMongoRepository;

    @Autowired
    private Environment environment;


    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
        String timeZone = environment.getProperty("TimeZone");
        TimeZone.setDefault(TimeZone.getTimeZone(timeZone));

        System.out.println("=============== Users: ===============");
        userMongoRepository.findAll().forEach(user -> System.out.println(user.getEmail() + " -> Google ID: " + user.getGoogleId() + " -> DB ID: " + user.getId()));
//        System.out.println("============== Quizzes: ==============");
//        quizMongoRepository.findAll().forEach(qu -> System.out.println(qu.getTitle() + " -> " + qu.getId()));

//        TestClient
//        try {
//            connect(quiz.getId()); //Very simple STOMPClient test
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }

    public void connect(String quizID) throws InterruptedException, ExecutionException, TimeoutException, IOException {

        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        WebSocketClient transport = new SockJsClient(transports);
        WebSocketStompClient stompClient = new WebSocketStompClient(transport);

        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        //stompClient.setMessageConverter(new StringMessageConverter());


        String url = "ws://localhost:8080/ws";
        StompSessionHandler sessionHandler = new MyStompSessionHandler(quizID);
        System.out.println("Trying to connect");
        StompSession stompSession = stompClient.connect(url, sessionHandler).get(10, TimeUnit.SECONDS);
        System.out.println("Connection was successful");

        stompSession.subscribe("/user/queue/reply", sessionHandler); // connecting to private channel


//        Thread.sleep(10); //wait some time to let sockets start up
//        stompSession.send("/game/join/5f918f6b894d6016707a019f", "Victor"); //First make invalid request
//
//        System.out.println("OLD GAME");
//        Thread.sleep(10); //wait some time to let sockets start up
//        stompSession.send("/game/join/5fb30e4f3070b14d537cad3b", "Victor"); //Make request for old GAME
//
//        System.out.println("Not existing GAME");
//        Thread.sleep(10); //wait some time to let sockets start up
//        stompSession.send("/game/join/5fb3037cad3b", "Victor"); //
//
//        System.out.println("Null GAME");
//        Thread.sleep(10); //wait some time to let sockets start up
//        stompSession.send("/game/join/", "Victor"); //Make request for null GAME
//
//        Thread.sleep(1000); //wait to see difference in console
//        stompSession.send("/game/join/" + quizID, "Victor"); //Second invalid request as game didn´t start


        Thread.sleep(5000); //wait some time to let the game start
        System.out.println("########################################################################");
        System.out.println("Trying to join 4 times: ");
        stompSession.send("/game/join/" + quizID, "Victor"); //Valid request as the game should move to activeGames
//        Thread.sleep(10);
//        stompSession.send("/game/join/" + quizID, "Victor"); // Should be rejected
//        Thread.sleep(10);
//        stompSession.send("/game/join/" + quizID, "Pascal"); // Should be rejected
//        Thread.sleep(10);
//        stompSession.send("/game/join/" + quizID, "Pascal"); // Should be rejected

        stompSession.subscribe("/results/room/" + quizID, sessionHandler); // subscribe to Channel for the questions
        System.out.println("Join room id: /results/room/" + quizID);


    }
}

class MyStompSessionHandler extends StompSessionHandlerAdapter {

    private String id;
    private StompSession actSession;

    public MyStompSessionHandler(String quizID) {
        this.id = quizID;
    }

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        this.actSession = session;

        System.out.println("Executed after successful connection...");

        //session.subscribe("/user/queue/reply", this); // connecting to private channel


        //session.subscribe("/results/room/" + this.id, this);
        //System.out.println("Join room id: /results/room/" + this.id);
        System.out.println("New session opened: " + session.getSessionId());
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        System.out.println("Exception Happened" + exception);
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        System.out.println("Received Header by STOMPClient " + headers.toString());
        return Object.class;
    }

    @SneakyThrows
    @Override
    public void handleFrame(StompHeaders headers, @Nullable Object payload) {

        System.out.println("Received Frame by STOMPClient");
        LinkedHashMap content = (LinkedHashMap) payload;

        System.out.println(content.toString());

        if (content.size() == 5) {
            //GameJoinResponse
            GameJoinResponse translate = new GameJoinResponse((String) content.get("code"), (boolean) content.get("correct"), LocalDateTime.parse((String) content.get("startingTime")), (String) content.get("quizTitle"), (String) content.get("quizDescription"));
            System.out.println(translate.getCode());

        } else if (content.size() == 2) {
            //Question
            LinkedHashMap getQuestionContent = (LinkedHashMap) content.get("model");
            MultipleChoicesModel finalQuestion = new MultipleChoicesModel((String) getQuestionContent.get("question"), (List<String>) getQuestionContent.get("answers"), null);

            if (finalQuestion.getQuestion().equals("Which one is Letter A?")) {
                return;
            }


            System.out.println("Answering Questions with 1");
            this.actSession.send("/game/answer/" + this.id, List.of(1, 2)); // Send it multiple times to see if it can handle this
            Thread.sleep(10);
            this.actSession.send("/game/answer/" + this.id, List.of(1, 2, 3, 4)); // Send it multiple times to see if it can handle this
            Thread.sleep(10);
            this.actSession.send("/game/answer/" + this.id, List.of(1)); // Send it multiple times to see if it can handle this
            Thread.sleep(10);
            this.actSession.send("/game/answer/" + this.id, List.of(2, 4)); // Send it multiple times to see if it can handle this

        } else if (content.size() == 4) {
            //result
            PlayerScore result = new PlayerScore((String) content.get("sessionID"), LocalDateTime.parse((String) content.get("connectAt")));
            result.setAnswers((ArrayList<SavedAnswer>) content.get("answers"));
            result.setNickname((String) content.get("nickname"));

        } else if (content.size() == 1) {
            //QuizEnded
            QuizEndedResponse end = new QuizEndedResponse((String) content.get("message"));
        }

    }


}