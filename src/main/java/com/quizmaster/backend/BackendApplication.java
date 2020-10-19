package com.quizmaster.backend;

import com.quizmaster.backend.entities.Model;
import com.quizmaster.backend.entities.MultipleChoicesModel;
import com.quizmaster.backend.entities.Question;
import com.quizmaster.backend.entities.Quiz;
import com.quizmaster.backend.repositories.QuizMongoRepository;
import com.quizmaster.backend.repositories.UserMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

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

		System.out.println("============== Users: ==============");
		//userMongoRepository.findAll().forEach(user -> System.out.println(user.getName() + " -> " + user.getId()));

		System.out.println("============== Quizzes: ==============");
		LocalDateTime random = LocalDateTime.of(2020, Month.NOVEMBER, 29, 20, 00, 00);
		Model m1 = new MultipleChoicesModel("Which one is Letter C?", List.of("A", "B", "C", "D"), List.of(3));
		Model m2 = new MultipleChoicesModel("Which one is Letter A?", List.of("A", "B", "C", "D"), List.of(1));

		Question q1 = new Question("multiple", m1);
		Question q2 = new Question("multiple", m2);
		Quiz quiz = new Quiz("Quiz 3", random, "Random Note", List.of(q1, q2));

		quizMongoRepository.save(quiz);

		//quizMongoRepository.findAll().forEach(qu -> System.out.println(qu.getTitle() + " -> " + qu.getId()));
		//System.out.println(quizMongoRepository.getById("5f8dc1c5c0f21a25764dc0e7"));
	}
}