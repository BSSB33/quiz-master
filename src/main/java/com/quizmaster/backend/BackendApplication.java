package com.quizmaster.backend;

import com.quizmaster.backend.repositories.QuizMongoRepository;
import com.quizmaster.backend.repositories.UserMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
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
        //User a = new User("User1");
        //userMongoRepository.save(a);

        System.out.println("=============== Users: ===============");
        userMongoRepository.findAll().forEach(user -> System.out.println(user.getName() + " -> " + user.getId()));

        System.out.println("============== Quizzes: ==============");
        quizMongoRepository.findAll().forEach(qu -> System.out.println(qu.getTitle() + " -> " + qu.getId()));
        System.out.println(quizMongoRepository.getById("5f8dc1c5c0f21a25764dc0e7"));


//		LocalDateTime random = LocalDateTime.of(2020, Month.NOVEMBER, 29, 20, 00, 00);
//		Model m1 = new MultipleChoicesModel("Which one is Letter C?", List.of("A", "B", "C", "D"), List.of(3));
//		Model m2 = new MultipleChoicesModel("Which one is Letter A?", List.of("A", "B", "C", "D"), List.of(1));
//
//		Question q1 = new Question("qm.multiple_choice", m1);
//		Question q2 = new Question("qm.multiple_choice", m2);
        //Quiz quiz = new Quiz("Quiz 1", "d",random, "Random Note", List.of(q1, q2));

        //quizMongoRepository.save(quiz);

        //User u1 = new User("Victor");
        //User u2 = new User("Gabor");

//		userMongoRepository.save(u1);
//		userMongoRepository.save(u2);


        try {
            connect(); //Very simple STOMPClient test
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void connect() throws InterruptedException, ExecutionException, TimeoutException, IOException {


        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        WebSocketClient transport = new SockJsClient(transports);
        WebSocketStompClient stompClient = new WebSocketStompClient(transport);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter()); //later if we want to use other classes than plaintext we should switch to MappingJackson2MessageConverter

        String url = "ws://localhost:8080/ws";
        StompSessionHandler sessionHandler = new MyStompSessionHandler();
        System.out.println("trying to connect");
        StompSession stompSession = stompClient.connect(url, sessionHandler).get(10, TimeUnit.SECONDS);
        System.out.println("Connection was successful");


        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        for (; ; ) {
            String line = in.readLine();
            if (line == null) break;
            if (line.length() == 0) continue;
            stompSession.send("/api/create", "Victor");
        }

    }
}

class MyStompSessionHandler extends StompSessionHandlerAdapter {

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        System.out.println("Executed after successful connection...");

        session.subscribe("/results/created", this);

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
