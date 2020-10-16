package com.quizmaster.backend;

import com.quizmaster.backend.entities.User;
import com.quizmaster.backend.repositories.UserMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication implements CommandLineRunner {

	@Autowired
	private UserMongoRepository userMongoRepository;

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}


	@Override
	public void run(String... args) throws Exception {
		User a = new User("Asd");

		userMongoRepository.save(a);
		userMongoRepository.deleteAll();

	}
}
