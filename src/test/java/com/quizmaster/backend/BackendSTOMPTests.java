package com.quizmaster.backend;

import com.quizmaster.backend.entities.Model;
import com.quizmaster.backend.entities.MultipleChoicesModel;
import com.quizmaster.backend.entities.Question;
import com.quizmaster.backend.entities.Quiz;
import com.quizmaster.backend.repositories.QuizMongoRepository;
import com.quizmaster.backend.repositories.UserMongoRepository;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.SECONDS;

//@TestPropertySource("classpath:application.properties")
//@RunWith(SpringJUnit4ClassRunner.class)
//@WebAppConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BackendSTOMPTests {

	@LocalServerPort
	private Integer port;

	@Autowired
	private UserMongoRepository userMongoRepository;

	@Autowired
	private QuizMongoRepository quizMongoRepository;


	String WEBSOCKET_URI = "ws://localhost:"+ this.port + "/ws";
	static final String WEBSOCKET_TOPIC = "/topic";

	BlockingQueue<LinkedHashMap> blockingQueue;
	WebSocketStompClient stompClient;

	@BeforeEach
	public void setup() {
		this.stompClient = new WebSocketStompClient(new SockJsClient(
				List.of(new WebSocketTransport(new StandardWebSocketClient()))));
		blockingQueue = new LinkedBlockingDeque<LinkedHashMap>();
		this.stompClient.setMessageConverter(new MappingJackson2MessageConverter());
	}

	@Test
	void joiningNotExistingGameID() throws InterruptedException, TimeoutException, ExecutionException {
		WEBSOCKET_URI = "ws://localhost:"+ this.port + "/ws";
		StompSession session = stompClient
				.connect(WEBSOCKET_URI, new StompSessionHandlerAdapter() {})
				.get(1, SECONDS);

		session.subscribe("/user/queue/reply", new MyStompSessionHandler()); // connecting to private channel

		session.send("/game/join/999999", "Victor"); //Valid request as the game should move to activeGames

		LinkedHashMap result = blockingQueue.poll(1, SECONDS);
		Assert.assertEquals("Quiz not found", result.get("code"));
		Assert.assertEquals(false, result.get("correct"));
		Assert.assertEquals(null, result.get("startingTime"));
		Assert.assertEquals(null, result.get("quizTitle"));
		Assert.assertEquals(null, result.get("quizDescription"));
	}


	@Test
	void joiningTooEarlyExistingGame() throws InterruptedException, TimeoutException, ExecutionException {
		WEBSOCKET_URI = "ws://localhost:"+ this.port + "/ws";
		StompSession session = stompClient
				.connect(WEBSOCKET_URI, new StompSessionHandlerAdapter() {})
				.get(1, SECONDS);

		session.subscribe("/user/queue/reply", new MyStompSessionHandler()); // connecting to private channel

		//Generating Quiz
		Model m1 = new MultipleChoicesModel("Which one is Letter C?", List.of("A", "B", "C", "D"), List.of(3));
		Question q1 = new Question("qm.multiple_choice", m1);

		LocalDateTime startingTime = LocalDateTime.now().plusSeconds(11);
		Quiz quiz = new Quiz("TESTQUIZFORTESTCASEJoiningTooEarlyExistingGame", "d", startingTime, "Random Note", List.of(q1));
		quizMongoRepository.save(quiz);

		Thread.sleep(100);
		session.send("/game/join/" + quiz.getId(), "Victor"); //Valid request as the game should move to activeGames

		LinkedHashMap result = blockingQueue.poll(1, SECONDS);
		Assert.assertEquals("Quiz will start", result.get("code"));
		Assert.assertEquals(false, result.get("correct"));
		Assert.assertEquals(startingTime.truncatedTo(ChronoUnit.MILLIS), LocalDateTime.parse((String) result.get("startingTime")).truncatedTo(ChronoUnit.MILLIS));
		Assert.assertEquals("TESTQUIZFORTESTCASEJoiningTooEarlyExistingGame", result.get("quizTitle"));
		Assert.assertEquals("d", result.get("quizDescription"));

		for (Quiz act : quizMongoRepository.findAll()) {
			if (act.getTitle().equals("TESTQUIZFORTESTCASEJoiningTooEarlyExistingGame")) {
				quizMongoRepository.deleteById(act.getId());
			}
		}
	}

	
	class MyStompSessionHandler extends StompSessionHandlerAdapter {

		private String id;
		private StompSession actSession;

		public MyStompSessionHandler() {
			super();
		}

		@Override
		public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
			this.actSession = session;
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

		@Override
		public void handleFrame(StompHeaders headers, @Nullable Object payload) {

			System.out.println("Received Frame by STOMPClient");
			LinkedHashMap content = (LinkedHashMap) payload;
			blockingQueue.add(content);
			System.out.println(content.toString());
		}
	}

}
