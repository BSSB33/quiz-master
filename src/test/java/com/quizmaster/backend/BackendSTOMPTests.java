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
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BackendSTOMPTests {

	private final long QUESTIONTIME = 20; // in Seconds
	private final long SCHEDULERATE = 1; // in Seconds
	private final long TIMEWINDOW = 300; // in Seconds
	private final String QUIZPLAYERNAME = "Victor";
	private final String QUIZOWNERNAME = "hxns9bZEv5Kh5Qe1LerTvo5ggcFmgoWn";
	private final String QUIZTITLE = "RhLp6E8vvfQgX24uwAy5rmwnHfT8fSdalsNPZYJa";
	private final String QUIZDESC = "This is a very nice development quiz that should only exist during some live testing";
	private final long QUIZSTARTDELAY = 10; //Quiz startingTime from now

	String WEBSOCKET_URI;

	@LocalServerPort
	private Integer port;

	@Autowired
	private QuizMongoRepository quizMongoRepository;

	BlockingQueue<LinkedHashMap> blockingQueue = new LinkedBlockingDeque<LinkedHashMap>();
	WebSocketStompClient stompClient;

	@BeforeEach
	public void setup() {
		//Build STOMPClient
		this.stompClient = new WebSocketStompClient(new SockJsClient(
				List.of(new WebSocketTransport(new StandardWebSocketClient()))));
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
		assert result != null;
		assertEquals("Quiz not found", result.get("code"));
		assertEquals(false, result.get("correct"));
		assertNull(result.get("startingTime"));
		assertNull(result.get("quizTitle"));
		assertNull(result.get("quizDescription"));
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

		//set the startinTime before the TIMEWINDOW
		// in Seconds
		LocalDateTime startingTime = LocalDateTime.now().plusSeconds(TIMEWINDOW +100);
		Quiz quiz = new Quiz(this.QUIZTITLE, this.QUIZDESC, startingTime, "Random Note", List.of(q1));
		quiz.setOwnerId(QUIZOWNERNAME);
		quizMongoRepository.save(quiz);

		Thread.sleep(100);
		session.send("/game/join/" + quiz.getId(), "Victor"); //Valid request as the game should move to activeGames

		LinkedHashMap result = blockingQueue.poll(1, SECONDS);
		assert result != null;
		assertEquals("Quiz will start", result.get("code"));
		assertEquals(false, result.get("correct"));
		assertEquals(startingTime.truncatedTo(ChronoUnit.MILLIS), LocalDateTime.parse((String) result.get("startingTime")).truncatedTo(ChronoUnit.MILLIS));
		assertEquals(this.QUIZTITLE, result.get("quizTitle"));
		assertEquals(this.QUIZDESC, result.get("quizDescription"));

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

		LocalDateTime startingTime = LocalDateTime.now().plusSeconds(QUIZSTARTDELAY);
		Quiz quiz = new Quiz(this.QUIZTITLE, this.QUIZDESC, startingTime, "Random Note", List.of(q1));
		quiz.setOwnerId(QUIZOWNERNAME);
		quizMongoRepository.save(quiz);

		Thread.sleep(SCHEDULERATE*1000); //waitingTime
		session.send("/game/join/" + quiz.getId(), "Victor"); //Valid request as the game should move to activeGames


		LinkedHashMap result = blockingQueue.poll(1, SECONDS);
		assert result != null;
		assertEquals("You were added", Objects.requireNonNull(result).get("code"));
		assertEquals(true, result.get("correct"));
		assertEquals(startingTime.truncatedTo(ChronoUnit.MILLIS), LocalDateTime.parse((String) result.get("startingTime")).truncatedTo(ChronoUnit.MILLIS));
		assertEquals(this.QUIZTITLE, result.get("quizTitle"));
		assertEquals(this.QUIZDESC, result.get("quizDescription"));

	}


	/*
		Join a QuizGame that exists and where the lobby is open and wait until it plays through and sends the results.
		During that the Client sends no answers.
	 */
	@Test
	void joiningAndParticipatingWholeRoundWithoutAnswers() throws InterruptedException, TimeoutException, ExecutionException, NullPointerException {
		WEBSOCKET_URI = "ws://localhost:"+ this.port + "/ws";
		MyStompSessionHandler stompHandler = new MyStompSessionHandler();
		StompSession session = stompClient
				.connect(WEBSOCKET_URI, stompHandler)
				.get(1, SECONDS);
		session.subscribe("/user/queue/reply", stompHandler); // connecting to private channel

		//Generating Quiz
		Model m1 = new MultipleChoicesModel("Which one is Letter C?", List.of("A", "B", "C", "D"), List.of(3));
		Question q1 = new Question("qm.multiple_choice", m1);

		LocalDateTime startingTime = LocalDateTime.now().plusSeconds(QUIZSTARTDELAY);
		Quiz quiz = new Quiz(this.QUIZTITLE, this.QUIZDESC, startingTime, "Random Note", List.of(q1));
		quiz.setOwnerId(QUIZOWNERNAME);
		quizMongoRepository.save(quiz);

		Thread.sleep(1000); //waitingTime
		session.subscribe("/results/room/" + quiz.getId(), stompHandler); // subscribe to Game Channel

		LocalDateTime joiningTime = LocalDateTime.now();
		session.send("/game/join/" + quiz.getId(), QUIZPLAYERNAME); //Valid request as the game should move to activeGames


		LinkedHashMap result = blockingQueue.poll(1, SECONDS);
		assert result != null;
		assertEquals("You were added", Objects.requireNonNull(result).get("code"));
		assertEquals(true, result.get("correct"));
		assertEquals(startingTime.truncatedTo(ChronoUnit.MILLIS), LocalDateTime.parse((String) result.get("startingTime")).truncatedTo(ChronoUnit.MILLIS));
		assertEquals(this.QUIZTITLE, result.get("quizTitle"));
		assertEquals(this.QUIZDESC, result.get("quizDescription"));

		Thread.sleep(QUIZSTARTDELAY*1000);

		//First question should be reached
		result = blockingQueue.poll(1, SECONDS);
		assert result != null;
		assertEquals("qm.multiple_choice", result.get("type"));
		LinkedHashMap question = (LinkedHashMap) result.get("model");
		assertEquals("Which one is Letter C?", question.get("question"));
		assertEquals(List.of("A","B","C","D"), question.get("answers"));


		Thread.sleep(QUESTIONTIME*1000);

		//Quiz Ended should be received
		result = blockingQueue.poll(1, SECONDS);
		assert result != null;
		assertEquals("Quiz ended", result.get("message"));


		Thread.sleep(100);
		//Results should be here
		result = blockingQueue.poll(2, SECONDS);
		assert result != null;

		List<Answer> expectedResults = new ArrayList<Answer>();
		expectedResults.add(Answer.NOTANSWERED);
		checkResult(quiz, result, joiningTime, stompHandler, expectedResults);

		Thread.sleep(2000);
		result = blockingQueue.poll(QUESTIONTIME+1, SECONDS);
		Assert.assertNull(result);

	}


	/*
		Join a QuizGame that exists and where the lobby is open and wait until it plays through and sends the results.
		During that the Client sends wrong answers.
	 */
	@Test
	void joiningAndParticipatingWholeRoundWithWrongAnswers() throws InterruptedException, TimeoutException, ExecutionException, NullPointerException {
		WEBSOCKET_URI = "ws://localhost:"+ this.port + "/ws";
		MyStompSessionHandler stompHandler = new MyStompSessionHandler();
		StompSession session = stompClient
				.connect(WEBSOCKET_URI, stompHandler)
				.get(1, SECONDS);
		session.subscribe("/user/queue/reply", stompHandler); // connecting to private channel

		//Generating Quiz
		Model m1 = new MultipleChoicesModel("Which one is Letter C?", List.of("A", "B", "C", "D"), List.of(3));
		Question q1 = new Question("qm.multiple_choice", m1);

		LocalDateTime startingTime = LocalDateTime.now().plusSeconds(QUIZSTARTDELAY);
		Quiz quiz = new Quiz(this.QUIZTITLE, this.QUIZDESC, startingTime, "Random Note", List.of(q1));
		quiz.setOwnerId(QUIZOWNERNAME);
		quizMongoRepository.save(quiz);

		Thread.sleep(1000); //waitingTime
		session.subscribe("/results/room/" + quiz.getId(), stompHandler); // subscribe to Game Channel

		LocalDateTime joiningTime = LocalDateTime.now();
		session.send("/game/join/" + quiz.getId(), QUIZPLAYERNAME); //Valid request as the game should move to activeGames


		LinkedHashMap result = blockingQueue.poll(1, SECONDS);
		assert result != null;
		assertEquals("You were added", result.get("code"));
		assertEquals(true, result.get("correct"));
		assertEquals(startingTime.truncatedTo(ChronoUnit.MILLIS), LocalDateTime.parse((String) result.get("startingTime")).truncatedTo(ChronoUnit.MILLIS));
		assertEquals(this.QUIZTITLE, result.get("quizTitle"));
		assertEquals(this.QUIZDESC, result.get("quizDescription"));

		Thread.sleep(QUIZSTARTDELAY*1000);

		//First question should be reached
		result = blockingQueue.poll(1, SECONDS);
		assert result != null;
		assertEquals("qm.multiple_choice", result.get("type"));
		LinkedHashMap question = (LinkedHashMap) result.get("model");
		assertEquals("Which one is Letter C?", question.get("question"));
		assertEquals(List.of("A","B","C","D"), question.get("answers"));

		Thread.sleep(100);
		session.send("/game/answer/" + quiz.getId(), List.of(1));
		Thread.sleep(100);

		//Receive Conformation for Answer
		result = blockingQueue.poll(1, SECONDS);
		assert result != null;
		assertEquals("Thanks for your answer", result.get("code"));
		assertEquals(true, result.get("correct"));

		Thread.sleep(QUESTIONTIME*1000);

		//Quiz Ended should be received
		result = blockingQueue.poll(1, SECONDS);
		assert result != null;
		assertEquals("Quiz ended", result.get("message"));

		Thread.sleep(100);
		//Results should be here
		result = blockingQueue.poll(2, SECONDS);
		assert result != null;
		//list expected scores
		List<Answer> expectedResults = new ArrayList<Answer>();
		expectedResults.add(Answer.INCORRECT);
		checkResult(quiz, result, joiningTime, stompHandler, expectedResults);

		Thread.sleep(2000);
		result = blockingQueue.poll(QUESTIONTIME+1, SECONDS);
		assertNull(result);

	}


	/*
    Join a QuizGame that exists and where the lobby is open and wait until it plays through and sends the results.
    During that the Client sends empty answers. The empty answer should be recognized as INCORRECT
	 */
	@Test
	void joiningAndParticipatingWholeRoundWithEmptyAnswers() throws InterruptedException, TimeoutException, ExecutionException, NullPointerException {
		WEBSOCKET_URI = "ws://localhost:"+ this.port + "/ws";
		MyStompSessionHandler stompHandler = new MyStompSessionHandler();
		StompSession session = stompClient
				.connect(WEBSOCKET_URI, stompHandler)
				.get(1, SECONDS);
		session.subscribe("/user/queue/reply", stompHandler); // connecting to private channel

		//Generating Quiz
		Model m1 = new MultipleChoicesModel("Which one is Letter C?", List.of("A", "B", "C", "D"), List.of());
		Question q1 = new Question("qm.multiple_choice", m1);

		LocalDateTime startingTime = LocalDateTime.now().plusSeconds(QUIZSTARTDELAY);
		Quiz quiz = new Quiz(this.QUIZTITLE, this.QUIZDESC, startingTime, "Random Note", List.of(q1));
		quiz.setOwnerId(QUIZOWNERNAME);
		quizMongoRepository.save(quiz);

		Thread.sleep(1000); //waitingTime
		session.subscribe("/results/room/" + quiz.getId(), stompHandler); // subscribe to Game Channel

		LocalDateTime joiningTime = LocalDateTime.now();
		session.send("/game/join/" + quiz.getId(), QUIZPLAYERNAME); //Valid request as the game should move to activeGames


		LinkedHashMap result = blockingQueue.poll(1, SECONDS);
		assert result != null;
		assertEquals("You were added", result.get("code"));
		assertEquals(true, result.get("correct"));
		assertEquals(startingTime.truncatedTo(ChronoUnit.MILLIS), LocalDateTime.parse((String) result.get("startingTime")).truncatedTo(ChronoUnit.MILLIS));
		assertEquals(this.QUIZTITLE, result.get("quizTitle"));
		assertEquals(this.QUIZDESC, result.get("quizDescription"));

		Thread.sleep(QUIZSTARTDELAY*1000);

		//First question should be reached
		result = blockingQueue.poll(1, SECONDS);
		assert result != null;
		assertEquals("qm.multiple_choice", result.get("type"));
		LinkedHashMap question = (LinkedHashMap) result.get("model");
		assertEquals("Which one is Letter C?", question.get("question"));
		assertEquals(List.of("A","B","C","D"), question.get("answers"));

		Thread.sleep(100);
		session.send("/game/answer/" + quiz.getId(), List.of(1));
		Thread.sleep(100);

		//Receive Conformation for Answer
		result = blockingQueue.poll(1, SECONDS);
		assert result != null;
		assertEquals("Thanks for your answer", result.get("code"));
		assertEquals(true, result.get("correct"));


		//Quiz Ended should be received
		result = blockingQueue.poll(25, SECONDS);
		assert result != null;
		assertEquals("Quiz ended", result.get("message"));

		Thread.sleep(100);
		//Results should be here
		result = blockingQueue.poll(10, SECONDS);
		assert result != null;
		//list expected scores
		List<Answer> expectedResults = new ArrayList<Answer>();
		expectedResults.add(Answer.INCORRECT);
		checkResult(quiz, result, joiningTime, stompHandler, expectedResults);

		result = blockingQueue.poll(QUESTIONTIME+1, SECONDS);
		assertNull(result);

	}


	/*
		Join a QuizGame that exists and where the lobby is open and wait until it plays through and sends the results.
		During that the Client sends correct answers.
	 */
	@Test
	void joiningAndParticipatingWholeRoundWithRightAnswers() throws InterruptedException, TimeoutException, ExecutionException, NullPointerException {
		WEBSOCKET_URI = "ws://localhost:"+ this.port + "/ws";
		MyStompSessionHandler stompHandler = new MyStompSessionHandler();
		StompSession session = stompClient
				.connect(WEBSOCKET_URI, stompHandler)
				.get(1, SECONDS);
		session.subscribe("/user/queue/reply", stompHandler); // connecting to private channel

		//Generating Quiz
		Model m1 = new MultipleChoicesModel("Which one is Letter C?", List.of("A", "B", "C", "D"), List.of(3));
		Question q1 = new Question("qm.multiple_choice", m1);

		LocalDateTime startingTime = LocalDateTime.now().plusSeconds(QUIZSTARTDELAY);
		Quiz quiz = new Quiz(this.QUIZTITLE, this.QUIZDESC, startingTime, "Random Note", List.of(q1));
		quiz.setOwnerId(QUIZOWNERNAME);
		quizMongoRepository.save(quiz);

		Thread.sleep(1000); //waitingTime
		session.subscribe("/results/room/" + quiz.getId(), stompHandler); // subscribe to Game Channel

		LocalDateTime joiningTime = LocalDateTime.now();
		session.send("/game/join/" + quiz.getId(), QUIZPLAYERNAME); //Valid request as the game should move to activeGames


		LinkedHashMap result = blockingQueue.poll(1, SECONDS);
		assert result != null;
		assertEquals("You were added", result.get("code"));
		assertEquals(true, result.get("correct"));
		assertEquals(startingTime.truncatedTo(ChronoUnit.MILLIS), LocalDateTime.parse((String) result.get("startingTime")).truncatedTo(ChronoUnit.MILLIS));
		assertEquals(this.QUIZTITLE, result.get("quizTitle"));
		assertEquals(this.QUIZDESC, result.get("quizDescription"));

		Thread.sleep(QUIZSTARTDELAY*1000);

		//First question should be reached
		result = blockingQueue.poll(1, SECONDS);
		assert result != null;
		assertEquals("qm.multiple_choice", result.get("type"));
		LinkedHashMap question = (LinkedHashMap) result.get("model");
		assertEquals("Which one is Letter C?", question.get("question"));
		assertEquals(List.of("A","B","C","D"), question.get("answers"));

		Thread.sleep(100);
		session.send("/game/answer/" + quiz.getId(), List.of(3));
		Thread.sleep(100);

		//Receive Conformation for Answer
		result = blockingQueue.poll(1, SECONDS);
		assert result != null;
		assertEquals("Thanks for your answer", result.get("code"));
		assertEquals(true, result.get("correct"));

		Thread.sleep(QUESTIONTIME*1000);

		//Quiz Ended should be received
		result = blockingQueue.poll(1, SECONDS);
		assert result != null;
		assertEquals("Quiz ended", result.get("message"));

		Thread.sleep(100);
		//Results should be here
		result = blockingQueue.poll(2, SECONDS);
		assert result != null;
		//list expected scores
		List<Answer> expectedResults = new ArrayList<Answer>();
		expectedResults.add(Answer.CORRECT);
		checkResult(quiz, result, joiningTime, stompHandler, expectedResults);

		Thread.sleep(10000);
		result = blockingQueue.poll(QUESTIONTIME+1, SECONDS);
		assertNull(result);
	}


	/*
		Try to participate in a active QuizGame without joining before.
	 */
	@Test
	void participatingWithoutJoining() throws InterruptedException, TimeoutException, ExecutionException, NullPointerException {
		WEBSOCKET_URI = "ws://localhost:"+ this.port + "/ws";
		MyStompSessionHandler stompHandler = new MyStompSessionHandler();
		StompSession session = stompClient
				.connect(WEBSOCKET_URI, stompHandler)
				.get(1, SECONDS);
		session.subscribe("/user/queue/reply", stompHandler); // connecting to private channel

		//Generating Quiz
		Model m1 = new MultipleChoicesModel("Which one is Letter C?", List.of("A", "B", "C", "D"), List.of(3));
		Question q1 = new Question("qm.multiple_choice", m1);

		LocalDateTime startingTime = LocalDateTime.now().plusSeconds(QUIZSTARTDELAY);
		Quiz quiz = new Quiz(this.QUIZTITLE, this.QUIZDESC, startingTime, "Random Note", List.of(q1));
		quiz.setOwnerId(QUIZOWNERNAME);
		quizMongoRepository.save(quiz);

		Thread.sleep(1000); //waitingTime
		session.subscribe("/results/room/" + quiz.getId(), stompHandler); // subscribe to Game Channel

		Thread.sleep(QUIZSTARTDELAY*1000);

		//First question should be reached
		LinkedHashMap result = blockingQueue.poll(1, SECONDS);
		assert result != null;
		assertEquals("qm.multiple_choice", result.get("type"));
		LinkedHashMap question = (LinkedHashMap) result.get("model");
		assertEquals("Which one is Letter C?", question.get("question"));
		assertEquals(List.of("A","B","C","D"), question.get("answers"));

		Thread.sleep(100);
		session.send("/game/answer/" + quiz.getId(), List.of(3));
		Thread.sleep(100);

		//Receive Error for not joining the game
		result = blockingQueue.poll(1, SECONDS);
		assert result != null;
		assertEquals("You did not join correctly to the game", result.get("code"));
		assertEquals(false, result.get("correct"));

		Thread.sleep(QUESTIONTIME*1000);

		//Quiz Ended should be received
		result = blockingQueue.poll(1, SECONDS);
		assert result != null;
		assertEquals("Quiz ended", result.get("message"));


		//no Results should be received because we did not join the QuizGame
		Thread.sleep(10000);
		result = blockingQueue.poll(QUESTIONTIME+1, SECONDS);
		assertNull(result);
	}


	/*
    Join a QuizGame late so that the first Question is missed. Afterwards answer one question incorrect, the next correct
    and do not answer the last question.
    Also send multiple answers to the same Question out to check if only the latest answer is taken into account
 	*/
	@Test
	void joiningLateAndSendDifferentAnswersMultipleTimes() throws InterruptedException, TimeoutException, ExecutionException, NullPointerException {
		WEBSOCKET_URI = "ws://localhost:"+ this.port + "/ws";
		MyStompSessionHandler stompHandler = new MyStompSessionHandler();
		StompSession session = stompClient
				.connect(WEBSOCKET_URI, stompHandler)
				.get(1, SECONDS);
//		session.subscribe("/user/queue/reply", stompHandler); // connecting to private channel

		//Generating Quiz
		Model m1 = new MultipleChoicesModel("Which one is Letter A?", List.of("A", "B", "C", "D"), List.of(1));
		Model m2 = new MultipleChoicesModel("Which one is Letter B?", List.of("A", "B", "C", "D"), List.of(2));
		Model m3 = new MultipleChoicesModel("Which one is Letter A,B,C,D?", List.of("A", "B", "C", "D"), List.of(1,2,3,4));
		Model m4 = new MultipleChoicesModel("Which one is Letter D?", List.of("A", "B", "C", "D"), List.of(4));
		Question q1 = new Question("qm.multiple_choice", m1);
		Question q2 = new Question("qm.multiple_choice", m2);
		Question q3 = new Question("qm.multiple_choice", m3);
		Question q4 = new Question("qm.multiple_choice", m4);

		LocalDateTime startingTime = LocalDateTime.now().plusSeconds(QUIZSTARTDELAY);
		Quiz quiz = new Quiz(this.QUIZTITLE, this.QUIZDESC, startingTime, "Random Note", List.of(q1, q2, q3, q4));
		quiz.setOwnerId(QUIZOWNERNAME);
		quizMongoRepository.save(quiz);

		//wait some time until QuizGame starts
		Thread.sleep(QUIZSTARTDELAY*1000+2000);
		//wait some time for the seconds question to come

		session.subscribe("/user/queue/reply", stompHandler); // connecting to private channel
		session.subscribe("/results/room/" + quiz.getId(), stompHandler);


		LocalDateTime joiningTime = LocalDateTime.now();
		session.send("/game/join/" + quiz.getId(), QUIZPLAYERNAME); //Valid request as the game should move to activeGames

		LinkedHashMap result = blockingQueue.poll(2, SECONDS);
		assert result != null;
		assertEquals("You were added", result.get("code"));
		assertEquals(true, result.get("correct"));
		assertEquals(startingTime.truncatedTo(ChronoUnit.MILLIS), LocalDateTime.parse((String) result.get("startingTime")).truncatedTo(ChronoUnit.MILLIS));
		assertEquals(this.QUIZTITLE, result.get("quizTitle"));
		assertEquals(this.QUIZDESC, result.get("quizDescription"));


		//Second question should be reached
		result = blockingQueue.poll(25, SECONDS);
		assert result != null;
		assertEquals("qm.multiple_choice", result.get("type"));
		LinkedHashMap question = (LinkedHashMap) result.get("model");
		assertEquals("Which one is Letter B?", question.get("question"));
		assertEquals(List.of("A","B","C","D"), question.get("answers"));

		Thread.sleep(200);
		//Answer question incorrectly
		session.send("/game/answer/" + quiz.getId(), List.of(1));
		//Receive Conformation for Answer
		result = blockingQueue.poll(5, SECONDS);
		assert result != null;
		assertEquals("Thanks for your answer", result.get("code"));
		assertEquals(true, result.get("correct"));
		Thread.sleep(200);

		//Answer question correctly
		session.send("/game/answer/" + quiz.getId(), List.of(2));
		//Receive Conformation for Answer
		result = blockingQueue.poll(5, SECONDS);
		assert result != null;
		assertEquals("Thanks for your answer", result.get("code"));
		assertEquals(true, result.get("correct"));
		Thread.sleep(200);

		//Answer question incorrectly
		session.send("/game/answer/" + quiz.getId(), List.of(1,2,3,4));
		//Receive Conformation for Answer
		result = blockingQueue.poll(5, SECONDS);
		assert result != null;
		assertEquals("Thanks for your answer", result.get("code"));
		assertEquals(true, result.get("correct"));


		//Third question should be reached
		result = blockingQueue.poll(25, SECONDS);
		assert result != null;
		assertEquals("qm.multiple_choice", result.get("type"));
		question = (LinkedHashMap) result.get("model");
		assertEquals("Which one is Letter A,B,C,D?", question.get("question"));
		assertEquals(List.of("A","B","C","D"), question.get("answers"));
		Thread.sleep(200);

		//Answer question correct
		session.send("/game/answer/" + quiz.getId(), List.of(1,2,3,4));
		//Receive Conformation for Answer
		result = blockingQueue.poll(5, SECONDS);
		assert result != null;
		assertEquals("Thanks for your answer", result.get("code"));
		assertEquals(true, result.get("correct"));
		Thread.sleep(200);

		//Answer question incorrect
		session.send("/game/answer/" + quiz.getId(), List.of(1,4));
		//Receive Conformation for Answer
		result = blockingQueue.poll(5, SECONDS);
		assert result != null;
		assertEquals("Thanks for your answer", result.get("code"));
		assertEquals(true, result.get("correct"));
		Thread.sleep(200);

		//Answer question incorrect
		session.send("/game/answer/" + quiz.getId(), List.of(2));
		//Receive Conformation for Answer
		result = blockingQueue.poll(5, SECONDS);
		assert result != null;
		assertEquals("Thanks for your answer", result.get("code"));
		assertEquals(true, result.get("correct"));
		Thread.sleep(200);

		//Answer question correct
		session.send("/game/answer/" + quiz.getId(), List.of(1,2,3,4));
		//Receive Conformation for Answer
		result = blockingQueue.poll(5, SECONDS);
		assert result != null;
		assertEquals("Thanks for your answer", result.get("code"));
		assertEquals(true, result.get("correct"));


		//Fourth question should be reached
		result = blockingQueue.poll(25, SECONDS);
		assert result != null;
		assertEquals("qm.multiple_choice", result.get("type"));
		question = (LinkedHashMap) result.get("model");
		assertEquals("Which one is Letter D?", question.get("question"));
		assertEquals(List.of("A","B","C","D"), question.get("answers"));


		//Quiz Ended should be received
		result = blockingQueue.poll(25, SECONDS);
		assert result != null;
		assertEquals("Quiz ended", result.get("message"));


		//Results should be here
		result = blockingQueue.poll(10, SECONDS);
		assert result != null;
		//list expected scores
		List<Answer> expectedResults = new ArrayList<Answer>();
		expectedResults.add(Answer.NOTANSWERED);
		expectedResults.add(Answer.INCORRECT);
		expectedResults.add(Answer.CORRECT);
		expectedResults.add(Answer.NOTANSWERED);
		checkResult(quiz, result, joiningTime, stompHandler, expectedResults);


		result = blockingQueue.poll(QUESTIONTIME+1, SECONDS);
		Assert.assertNull(result);
	}


	/*
	Build up two STOMPClients that both try to join a QuizGame with the same Nickname
	The seconds Client should get rejected. To ensure the function is working on the backend,
	the rejected clients tries to answer the question and should get a NotJoined Error. The first
	Clients also sends out an answer and it should be processed.
 	*/
	@Test
	void tryingToClaimSameNickname() throws InterruptedException, TimeoutException, ExecutionException, NullPointerException {
		WEBSOCKET_URI = "ws://localhost:"+ this.port + "/ws";
		MyStompSessionHandler stompHandler = new MyStompSessionHandler();
		StompSession session = stompClient
				.connect(WEBSOCKET_URI, stompHandler)
				.get(1, SECONDS);
		session.subscribe("/user/queue/reply", stompHandler); // connecting to private channel

		//Build second STOMPClient
		WebSocketStompClient secondClient = new WebSocketStompClient(new SockJsClient(
				List.of(new WebSocketTransport(new StandardWebSocketClient()))));
		secondClient.setMessageConverter(new MappingJackson2MessageConverter());
		//connect second STOMPClient
		StompSession secondSession = stompClient
				.connect(WEBSOCKET_URI, stompHandler)
				.get(1, SECONDS);
		secondSession.subscribe("/user/queue/reply", stompHandler); // connecting to private channel

		//Generating Quiz
		Model m1 = new MultipleChoicesModel("Which one is Letter C?", List.of("A", "B", "C", "D"), List.of(3));
		Question q1 = new Question("qm.multiple_choice", m1);
		LocalDateTime startingTime = LocalDateTime.now().plusSeconds(QUIZSTARTDELAY);
		Quiz quiz = new Quiz(this.QUIZTITLE, this.QUIZDESC, startingTime, "Random Note", List.of(q1));
		quizMongoRepository.save(quiz);

		Thread.sleep(1000); //waitingTime

		//Join Request from first Client should be accepted
		session.send("/game/join/" + quiz.getId(), QUIZPLAYERNAME); //Valid request as the game should move to activeGames

		LinkedHashMap result = blockingQueue.poll(1, SECONDS);
		assert result != null;
		assertEquals("You were added", result.get("code"));
		assertEquals(true, result.get("correct"));
		assertEquals(startingTime.truncatedTo(ChronoUnit.MILLIS), LocalDateTime.parse((String) result.get("startingTime")).truncatedTo(ChronoUnit.MILLIS));
		assertEquals(this.QUIZTITLE, result.get("quizTitle"));
		assertEquals(this.QUIZDESC, result.get("quizDescription"));

		//Join request from secondClient should be rejected
		secondSession.send("/game/join/" + quiz.getId(), QUIZPLAYERNAME); //Valid request as the game should move to activeGames
		result = blockingQueue.poll(1, SECONDS);
		assert result != null;
		assertEquals("Nickname already given out", result.get("code"));
		assertEquals(false, result.get("correct"));
		assertEquals(startingTime.truncatedTo(ChronoUnit.MILLIS), LocalDateTime.parse((String) result.get("startingTime")).truncatedTo(ChronoUnit.MILLIS));
		assertEquals(this.QUIZTITLE, result.get("quizTitle"));
		assertEquals(this.QUIZDESC, result.get("quizDescription"));

		secondSession.subscribe("/results/room/" + quiz.getId(), stompHandler); // subscribe to Game Channel
		//wait until QuizGame should send questions out
		Thread.sleep(QUIZSTARTDELAY*1000);
		result = blockingQueue.poll(10, SECONDS);
		assert result != null;
		assertEquals("qm.multiple_choice", result.get("type"));
		LinkedHashMap question = (LinkedHashMap) result.get("model");
		assertEquals("Which one is Letter C?", question.get("question"));
		assertEquals(List.of("A","B","C","D"), question.get("answers"));

		//Send out answer with first Client - should work
		session.send("/game/answer/" + quiz.getId(), List.of(3));
		Thread.sleep(100);

		//Receive Error for not joining the game
		result = blockingQueue.poll(1, SECONDS);
		assert result != null;
		assertEquals("Thanks for your answer", result.get("code"));
		assertEquals(true, result.get("correct"));

		//Send out answer with second Client - should not work
		secondSession.send("/game/answer/" + quiz.getId(), List.of(3));
		Thread.sleep(100);

		//Receive Error for not joining the game
		result = blockingQueue.poll(1, SECONDS);
		assert result != null;
		assertEquals("You did not join correctly to the game", result.get("code"));
		assertEquals(false, result.get("correct"));

	}


	public void checkResult(Quiz quiz,  LinkedHashMap serverResponse, LocalDateTime joiningTime, MyStompSessionHandler stompHandler, List<Answer> expectedScore){

		//first check individual player score
		assert quiz != null;
		assert serverResponse != null;

		LinkedHashMap individualResults = (LinkedHashMap) serverResponse.get("individualResult");

		assertEquals(QUIZPLAYERNAME, individualResults.get("nickname"));
		//check if diff between joiningTime local and on backend is not too big
		String serverTime = (String) individualResults.get("connectAt");
		long seconds = ChronoUnit.SECONDS.between(joiningTime, LocalDateTime.parse(serverTime).truncatedTo(ChronoUnit.SECONDS));
		boolean smalldiff = false;
		if(seconds == 0 || seconds == 1){
			smalldiff = true;
		}
		assertTrue(smalldiff);
		//Check SessionID
		boolean sameSessionID = false;
		String serverSessionID = (String) individualResults.get("sessionID");
		if (stompHandler.getSTOMPHaderSessionID().startsWith(serverSessionID) && serverSessionID.length() > 8){
			sameSessionID = true;
		}
		assertTrue(sameSessionID);

		ArrayList<LinkedHashMap> savedAnswers = (ArrayList<LinkedHashMap>) individualResults.get("answers");

		//control if expectedResults is correct
		for (int j = 0; j < expectedScore.size(); j++){
			assertTrue(expectedScore.get(j).toString().equals(savedAnswers.get(j).get("isCorrect")));
			assertTrue(j == (int) savedAnswers.get(j).get("questionNumber"));
		}


		//now check if public question content is correct

		ArrayList<LinkedHashMap> publicQuestions = (ArrayList<LinkedHashMap>) serverResponse.get("publicQuestions");

		//check if both lists are holding the same amount of questions
		assertEquals(quiz.getQuestions().size(), publicQuestions.size());

		for (int i = 0; i< quiz.getQuestions().size(); i++){
			assertEquals(quiz.getQuestions().get(i).getType(), publicQuestions.get(i).get("type"));

			//get model content/questions
			LinkedHashMap questionContent = (LinkedHashMap) publicQuestions.get(i).get("model");

			MultipleChoicesModel actQuestions = (MultipleChoicesModel) quiz.getQuestions().get(i).getModel();

			assertEquals(actQuestions.getQuestion(), questionContent.get("question"));
			assertEquals(actQuestions.getAnswers(), questionContent.get("answers"));
			assertEquals(actQuestions.getCorrectAnswers(), questionContent.get("correctAnswers"));
		}

	}

	/*
	STOMPSessionHandler to get the Frame Data and process it in the test cases
	 */
	class MyStompSessionHandler extends StompSessionHandlerAdapter {

		private StompHeaders connectedHeaders;

		public MyStompSessionHandler() {
			super();
		}

		public String getSTOMPHaderSessionID(){
			return this.connectedHeaders.getMessageId();
		}

		@Override
		public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
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
			assert content != null;
			blockingQueue.add(content);
			System.out.println(content.toString());
			this.connectedHeaders = headers;
		}
	}

}
