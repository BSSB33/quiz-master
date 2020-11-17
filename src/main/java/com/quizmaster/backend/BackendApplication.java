package com.quizmaster.backend;

import com.quizmaster.backend.entities.*;
import com.quizmaster.backend.repositories.QuizMongoRepository;
import com.quizmaster.backend.repositories.UserMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.SimpleMessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@SpringBootApplication
public class BackendApplication implements CommandLineRunner {

    @Autowired
    private UserMongoRepository userMongoRepository;

    @Autowired
    private QuizMongoRepository quizMongoRepository;

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        System.out.println("=============== Users: ===============");
        userMongoRepository.findAll().forEach(user -> System.out.println(user.getEmail() + " -> Google ID: " + user.getGoogleId() +  " -> DB ID: " + user.getId()));

        System.out.println("============== Quizzes: ==============");
        quizMongoRepository.findAll().forEach(qu -> System.out.println(qu.getTitle() + " -> " + qu.getId()));

		LocalDateTime random = LocalDateTime.of(2020, Month.NOVEMBER, 29, 20, 00, 00);
		Model m1 = new MultipleChoicesModel("Which one is Letter C?", List.of("A", "B", "C", "D"), List.of(3));
		Model m2 = new MultipleChoicesModel("Which one is Letter A?", List.of("A", "B", "C", "D"), List.of(1));
		Question q1 = new Question("qm.multiple_choice", m1);
		Question q2 = new Question("qm.multiple_choice", m2);

        for (Quiz Act : quizMongoRepository.findAll()){
            if (Act.getTitle().equals("Testquiz")){
                quizMongoRepository.deleteById(Act.getId());
            }
        }

        Quiz quiz = new Quiz("Testquiz", "d",LocalDateTime.now().plusMinutes(2), "Random Note", List.of(q1, q2));
        quizMongoRepository.save(quiz);

        try {
            connect(quiz.getId()); //Very simple STOMPClient test
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void connect(String quizID) throws InterruptedException, ExecutionException, TimeoutException, IOException {

        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        WebSocketClient transport = new SockJsClient(transports);
        WebSocketStompClient stompClient = new WebSocketStompClient(transport);
        //stompClient.setMessageConverter(new MappingJackson2MessageConverter()); //later if we want to use other classes than plaintext we should switch to MappingJackson2MessageConverter
        stompClient.setMessageConverter(new StringMessageConverter());


        String url = "ws://localhost:8080/ws";
        StompSessionHandler sessionHandler = new MyStompSessionHandler(quizID);
        System.out.println("Trying to connect");
        StompSession stompSession = stompClient.connect(url, sessionHandler).get(10, TimeUnit.SECONDS);
        System.out.println("Connection was successful");


        Thread.sleep(10); //wait some time to let sockets start up
        stompSession.send("/game/join/5f918f6b894d6016707a019f", "Victor"); //First make invalid request

        Thread.sleep(1000); //wait some time to let sockets start up
        stompSession.send("/game/join/" + quizID, "Victor"); //Second invalid request as game didn´t start

        Thread.sleep(60000); //wait some time to let sockets start up
        stompSession.send("/game/join/" + quizID, "Victor"); //Valid request as the game should move to activeGames

        //stompSession.subscribe("/results/room/" + quizID, sessionHandler);
        //System.out.println("Join room id: /results/room/" + quizID);

        //stompSession.send("/game/join/5f918f6b894d6016707a019f", "Victor"); //Demo quiz
    }
}

class MyStompSessionHandler extends StompSessionHandlerAdapter {

    private String id;

    public MyStompSessionHandler(String quizID) {
        this.id = quizID;
    }

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        System.out.println("Executed after successful connection...");

        session.subscribe("/user/queue/reply", this); // connecting to private channel

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
        return String.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, @Nullable Object payload) {
        System.out.println("Received Frame by STOMPClient");
        System.out.println("Received: " + (payload.toString()));
    }


}