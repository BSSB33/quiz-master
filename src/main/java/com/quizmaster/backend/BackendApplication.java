package com.quizmaster.backend;

import com.quizmaster.backend.entities.Model;
import com.quizmaster.backend.entities.Quiz;
import com.quizmaster.backend.entities.User;
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
		userMongoRepository.findAll().forEach(user -> System.out.println(user.getName() + " -> " + user.getId()));

		System.out.println("============== Quizzes: ==============");

		//LocalDateTime random = LocalDateTime.of(2020, Month.NOVEMBER, 29, 20, 00, 00);
		//Model m1 = new Model("Which one is Letter C?", List.of("A", "B", "C", "D"), List.of(3));
		//Model m2 = new Model("Which one is Letter A?", List.of("A", "B", "C", "D"), List.of(1));
		//Quiz q1 = new Quiz("Quiz 1", random, "Random Note", List.of(m1, m2));

		//quizMongoRepository.save(q1);

		quizMongoRepository.findAll().forEach(quiz -> System.out.println(quiz.getTitle() + " -> " + quiz.getId()));
		quizMongoRepository.getById("5f8dacd12e37ae6c1086829f");
	}
}