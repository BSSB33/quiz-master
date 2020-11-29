package com.quizmaster.backend;

import com.quizmaster.backend.entities.*;
import com.quizmaster.backend.repositories.QuizMongoRepository;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
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
import java.util.ArrayList;
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

	private final long TIMEWINDOW = 300; // in Seconds
	private final long QUESTIONTIME = 20; // in Seconds
	private final long SCHEDULERATE = 1; // in Seconds
	private final String QUIZPLAYERNAME = "Victor";
	private final String QUIZTITLE = "RhLp6E8vvfQgX24uwAy5rmwnHfT8fSdalsNPZYJa";
	private final String QUIZDESC = "This is a very nice development quiz that should only existis during some live testing";

	Quiz shortQuiz;
	Quiz longQuiz;
	String WEBSOCKET_URI = "ws://localhost:"+ this.port + "/ws";


	@LocalServerPort
	private Integer port;

	@Autowired
	private QuizMongoRepository quizMongoRepository;

	BlockingQueue<LinkedHashMap> blockingQueue;
	WebSocketStompClient stompClient;

	@BeforeEach
	public void setup() {
		//Build STOMPClient
		this.stompClient = new WebSocketStompClient(new SockJsClient(
				List.of(new WebSocketTransport(new StandardWebSocketClient()))));
		blockingQueue = new LinkedBlockingDeque<LinkedHashMap>();
		this.stompClient.setMessageConverter(new MappingJackson2MessageConverter());
	}

	@AfterEach
	public void cleanUpEach(){
		for (Quiz act : quizMongoRepository.findAll()) {
			if (act.getTitle().equals(QUIZTITLE)) {
				quizMongoRepository.deleteById(act.getId());
			}
		}
	}

	/*
		Try to join a non existing QuizGame
	 */
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

	/*
		Try to join an existing QuizGame, but it has not started already and there is no lobby open.
	 */
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

		LocalDateTime startingTime = LocalDateTime.now().plusSeconds(TIMEWINDOW+100);
		Quiz quiz = new Quiz(this.QUIZTITLE, this.QUIZDESC, startingTime, "Random Note", List.of(q1));
		quizMongoRepository.save(quiz);

		Thread.sleep(100);
		session.send("/game/join/" + quiz.getId(), "Victor"); //Valid request as the game should move to activeGames

		LinkedHashMap result = blockingQueue.poll(1, SECONDS);
		Assert.assertEquals("Quiz will start", result.get("code"));
		Assert.assertEquals(false, result.get("correct"));
		Assert.assertEquals(startingTime.truncatedTo(ChronoUnit.MILLIS), LocalDateTime.parse((String) result.get("startingTime")).truncatedTo(ChronoUnit.MILLIS));
		Assert.assertEquals(this.QUIZTITLE, result.get("quizTitle"));
		Assert.assertEquals(this.QUIZDESC, result.get("quizDescription"));

	}

	/*
		Join a QuizGame that exists and where the lobby is open
	 */
	@Test
	void joiningActualGame() throws InterruptedException, TimeoutException, ExecutionException {
		WEBSOCKET_URI = "ws://localhost:"+ this.port + "/ws";
		StompSession session = stompClient
				.connect(WEBSOCKET_URI, new StompSessionHandlerAdapter() {})
				.get(1, SECONDS);

		session.subscribe("/user/queue/reply", new MyStompSessionHandler()); // connecting to private channel

		//Generating Quiz
		Model m1 = new MultipleChoicesModel("Which one is Letter C?", List.of("A", "B", "C", "D"), List.of(3));
		Question q1 = new Question("qm.multiple_choice", m1);

		LocalDateTime startingTime = LocalDateTime.now().plusSeconds(30);
		Quiz quiz = new Quiz(this.QUIZTITLE, this.QUIZDESC, startingTime, "Random Note", List.of(q1));
		quizMongoRepository.save(quiz);

		Thread.sleep(SCHEDULERATE*1000); //waitingTime
		session.send("/game/join/" + quiz.getId(), "Victor"); //Valid request as the game should move to activeGames


		LinkedHashMap result = blockingQueue.poll(1, SECONDS);
		Assert.assertEquals("You were added", result.get("code"));
		Assert.assertEquals(true, result.get("correct"));
		Assert.assertEquals(startingTime.truncatedTo(ChronoUnit.MILLIS), LocalDateTime.parse((String) result.get("startingTime")).truncatedTo(ChronoUnit.MILLIS));
		Assert.assertEquals(this.QUIZTITLE, result.get("quizTitle"));
		Assert.assertEquals(this.QUIZDESC, result.get("quizDescription"));

	}


	/*
		Join a QuizGame that exists and where the lobby is open and wait until it plays through and sends the results.
		During that the Client sends no answers.
	 */
	@Test
	void joiningAndParticipatingWholeRoundWithoutAnswers() throws InterruptedException, TimeoutException, ExecutionException {
		WEBSOCKET_URI = "ws://localhost:"+ this.port + "/ws";
		MyStompSessionHandler stompHandler = new MyStompSessionHandler();
		StompSession session = stompClient
				.connect(WEBSOCKET_URI, stompHandler)
				.get(1, SECONDS);
		session.subscribe("/user/queue/reply", stompHandler); // connecting to private channel

		//Generating Quiz
		Model m1 = new MultipleChoicesModel("Which one is Letter C?", List.of("A", "B", "C", "D"), List.of(3));
		Question q1 = new Question("qm.multiple_choice", m1);

		LocalDateTime startingTime = LocalDateTime.now().plusSeconds(30);
		Quiz quiz = new Quiz(this.QUIZTITLE, this.QUIZDESC, startingTime, "Random Note", List.of(q1));
		quizMongoRepository.save(quiz);

		Thread.sleep(1000); //waitingTime
		session.subscribe("/results/room/" + quiz.getId(), stompHandler); // subscribe to Game Channel

		LocalDateTime joiningTime = LocalDateTime.now();
		session.send("/game/join/" + quiz.getId(), QUIZPLAYERNAME); //Valid request as the game should move to activeGames


		LinkedHashMap result = blockingQueue.poll(1, SECONDS);
		Assert.assertEquals("You were added", result.get("code"));
		Assert.assertEquals(true, result.get("correct"));
		Assert.assertEquals(startingTime.truncatedTo(ChronoUnit.MILLIS), LocalDateTime.parse((String) result.get("startingTime")).truncatedTo(ChronoUnit.MILLIS));
		Assert.assertEquals(this.QUIZTITLE, result.get("quizTitle"));
		Assert.assertEquals(this.QUIZDESC, result.get("quizDescription"));

		Thread.sleep(30000);

		//First question should be reached
		result = blockingQueue.poll(1, SECONDS);
		Assert.assertEquals("qm.multiple_choice", result.get("type"));
		LinkedHashMap question = (LinkedHashMap) result.get("model");
		Assert.assertEquals("Which one is Letter C?", question.get("question"));
		Assert.assertEquals(List.of("A","B","C","D"), question.get("answers"));


		Thread.sleep(QUESTIONTIME*1000);

		//Quiz Ended should be received
		result = blockingQueue.poll(1, SECONDS);
		Assert.assertEquals("Quiz ended", result.get("message"));

		Thread.sleep(100);
		//Results should be here
		result = blockingQueue.poll(2, SECONDS);
		Assert.assertEquals(QUIZPLAYERNAME, result.get("nickname"));
		//check if diff between joiningTime local and on backend is not too big
		long seconds = ChronoUnit.SECONDS.between(joiningTime, LocalDateTime.parse((String) result.get("connectAt")).truncatedTo(ChronoUnit.SECONDS));
		boolean smalldiff = false;
		if(seconds == 0 || seconds == 1){
			smalldiff = true;
		}
		Assert.assertEquals(smalldiff, true);
		//Check SessionID
		Assert.assertEquals(stompHandler.getSTOMPHaderSessionID(), result.get("sessionID"));
		ArrayList<LinkedHashMap> savedAnswers = (ArrayList<LinkedHashMap>) result.get("answers");
		Assert.assertEquals(0, savedAnswers.get(0).get("questionNumber"));
		Assert.assertEquals(Answer.NOTANSWERED.toString(), savedAnswers.get(0).get("isCorrect"));

		Thread.sleep(2000);
		result = blockingQueue.poll(QUESTIONTIME+1, SECONDS);
		Assert.assertEquals(null, result);

	}



	/*
		Join a QuizGame that exists and where the lobby is open and wait until it plays through and sends the results.
		During that the Client sends wrong answers.
	 */
	@Test
	void joiningAndParticipatingWholeRoundWithWrongAnswers() throws InterruptedException, TimeoutException, ExecutionException {
		WEBSOCKET_URI = "ws://localhost:"+ this.port + "/ws";
		MyStompSessionHandler stompHandler = new MyStompSessionHandler();
		StompSession session = stompClient
				.connect(WEBSOCKET_URI, stompHandler)
				.get(1, SECONDS);
		session.subscribe("/user/queue/reply", stompHandler); // connecting to private channel

		//Generating Quiz
		Model m1 = new MultipleChoicesModel("Which one is Letter C?", List.of("A", "B", "C", "D"), List.of(3));
		Question q1 = new Question("qm.multiple_choice", m1);

		LocalDateTime startingTime = LocalDateTime.now().plusSeconds(30);
		Quiz quiz = new Quiz(this.QUIZTITLE, this.QUIZDESC, startingTime, "Random Note", List.of(q1));
		quizMongoRepository.save(quiz);

		Thread.sleep(1000); //waitingTime
		session.subscribe("/results/room/" + quiz.getId(), stompHandler); // subscribe to Game Channel

		LocalDateTime joiningTime = LocalDateTime.now();
		session.send("/game/join/" + quiz.getId(), QUIZPLAYERNAME); //Valid request as the game should move to activeGames


		LinkedHashMap result = blockingQueue.poll(1, SECONDS);
		Assert.assertEquals("You were added", result.get("code"));
		Assert.assertEquals(true, result.get("correct"));
		Assert.assertEquals(startingTime.truncatedTo(ChronoUnit.MILLIS), LocalDateTime.parse((String) result.get("startingTime")).truncatedTo(ChronoUnit.MILLIS));
		Assert.assertEquals(this.QUIZTITLE, result.get("quizTitle"));
		Assert.assertEquals(this.QUIZDESC, result.get("quizDescription"));

		Thread.sleep(30000);

		//First question should be reached
		result = blockingQueue.poll(1, SECONDS);
		Assert.assertEquals("qm.multiple_choice", result.get("type"));
		LinkedHashMap question = (LinkedHashMap) result.get("model");
		Assert.assertEquals("Which one is Letter C?", question.get("question"));
		Assert.assertEquals(List.of("A","B","C","D"), question.get("answers"));

		Thread.sleep(100);
		session.send("/game/answer/" + quiz.getId(), List.of(1));
		Thread.sleep(100);

		//Receive Conformation for Answer
		result = blockingQueue.poll(1, SECONDS);
		Assert.assertEquals("Thanks for your answer", result.get("code"));
		Assert.assertEquals(true, result.get("correct"));

		Thread.sleep(QUESTIONTIME*1000);

		//Quiz Ended should be received
		result = blockingQueue.poll(1, SECONDS);
		Assert.assertEquals("Quiz ended", result.get("message"));

		Thread.sleep(100);
		//Results should be here
		result = blockingQueue.poll(2, SECONDS);
		Assert.assertEquals(QUIZPLAYERNAME, result.get("nickname"));
		//check if diff between joiningTime local and on backend is not too big
		long seconds = ChronoUnit.SECONDS.between(joiningTime, LocalDateTime.parse((String) result.get("connectAt")).truncatedTo(ChronoUnit.SECONDS));
		boolean smalldiff = false;
		if(seconds == 0 || seconds == 1){
			smalldiff = true;
		}
		Assert.assertEquals(smalldiff, true);
		//Check SessionID
		Assert.assertEquals(stompHandler.getSTOMPHaderSessionID(), result.get("sessionID"));
		ArrayList<LinkedHashMap> savedAnswers = (ArrayList<LinkedHashMap>) result.get("answers");
		Assert.assertEquals(0, savedAnswers.get(0).get("questionNumber"));
		Assert.assertEquals(Answer.INCORRECT.toString(), savedAnswers.get(0).get("isCorrect"));

		Thread.sleep(2000);
		result = blockingQueue.poll(QUESTIONTIME+1, SECONDS);
		Assert.assertEquals(null, result);

	}

	/*
		Join a QuizGame that exists and where the lobby is open and wait until it plays through and sends the results.
		During that the Client sends correct answers.
	 */
	@Test
	void joiningAndParticipatingWholeRoundWithRightAnswers() throws InterruptedException, TimeoutException, ExecutionException {
		WEBSOCKET_URI = "ws://localhost:"+ this.port + "/ws";
		MyStompSessionHandler stompHandler = new MyStompSessionHandler();
		StompSession session = stompClient
				.connect(WEBSOCKET_URI, stompHandler)
				.get(1, SECONDS);
		session.subscribe("/user/queue/reply", stompHandler); // connecting to private channel

		//Generating Quiz
		Model m1 = new MultipleChoicesModel("Which one is Letter C?", List.of("A", "B", "C", "D"), List.of(3));
		Question q1 = new Question("qm.multiple_choice", m1);

		LocalDateTime startingTime = LocalDateTime.now().plusSeconds(30);
		Quiz quiz = new Quiz(this.QUIZTITLE, this.QUIZDESC, startingTime, "Random Note", List.of(q1));
		quizMongoRepository.save(quiz);

		Thread.sleep(1000); //waitingTime
		session.subscribe("/results/room/" + quiz.getId(), stompHandler); // subscribe to Game Channel

		LocalDateTime joiningTime = LocalDateTime.now();
		session.send("/game/join/" + quiz.getId(), QUIZPLAYERNAME); //Valid request as the game should move to activeGames


		LinkedHashMap result = blockingQueue.poll(1, SECONDS);
		Assert.assertEquals("You were added", result.get("code"));
		Assert.assertEquals(true, result.get("correct"));
		Assert.assertEquals(startingTime.truncatedTo(ChronoUnit.MILLIS), LocalDateTime.parse((String) result.get("startingTime")).truncatedTo(ChronoUnit.MILLIS));
		Assert.assertEquals(this.QUIZTITLE, result.get("quizTitle"));
		Assert.assertEquals(this.QUIZDESC, result.get("quizDescription"));

		Thread.sleep(30000);

		//First question should be reached
		result = blockingQueue.poll(1, SECONDS);
		Assert.assertEquals("qm.multiple_choice", result.get("type"));
		LinkedHashMap question = (LinkedHashMap) result.get("model");
		Assert.assertEquals("Which one is Letter C?", question.get("question"));
		Assert.assertEquals(List.of("A","B","C","D"), question.get("answers"));

		Thread.sleep(100);
		session.send("/game/answer/" + quiz.getId(), List.of(3));
		Thread.sleep(100);

		//Receive Conformation for Answer
		result = blockingQueue.poll(1, SECONDS);
		Assert.assertEquals("Thanks for your answer", result.get("code"));
		Assert.assertEquals(true, result.get("correct"));

		Thread.sleep(QUESTIONTIME*1000);

		//Quiz Ended should be received
		result = blockingQueue.poll(1, SECONDS);
		Assert.assertEquals("Quiz ended", result.get("message"));

		Thread.sleep(100);
		//Results should be here
		result = blockingQueue.poll(2, SECONDS);
		Assert.assertEquals(QUIZPLAYERNAME, result.get("nickname"));
		//check if diff between joiningTime local and on backend is not too big
		long seconds = ChronoUnit.SECONDS.between(joiningTime, LocalDateTime.parse((String) result.get("connectAt")).truncatedTo(ChronoUnit.SECONDS));
		boolean smalldiff = false;
		if(seconds == 0 || seconds == 1){
			smalldiff = true;
		}
		Assert.assertEquals(smalldiff, true);
		//Check SessionID
		Assert.assertEquals(stompHandler.getSTOMPHaderSessionID(), result.get("sessionID"));
		ArrayList<LinkedHashMap> savedAnswers = (ArrayList<LinkedHashMap>) result.get("answers");
		Assert.assertEquals(0, savedAnswers.get(0).get("questionNumber"));
		Assert.assertEquals(Answer.CORRECT.toString(), savedAnswers.get(0).get("isCorrect"));

		Thread.sleep(2000);
		result = blockingQueue.poll(QUESTIONTIME+1, SECONDS);
		Assert.assertEquals(null, result);
	}




	
	class MyStompSessionHandler extends StompSessionHandlerAdapter {

		private String id;
		private StompSession actSession;
		private StompHeaders connectedHeaders;

		public MyStompSessionHandler() {
			super();
		}

		public String getSTOMPHaderSessionID(){
			String messageID = this.connectedHeaders.getMessageId();

			return messageID.substring(0, messageID.length()-2);
		}

		@Override
		public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
			this.actSession = session;
			this.connectedHeaders = connectedHeaders;
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
			this.connectedHeaders = headers;
		}
	}

}
