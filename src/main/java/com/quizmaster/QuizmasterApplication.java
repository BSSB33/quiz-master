package com.quizmaster;


import com.quizmaster.entities.User;
import com.quizmaster.repositories.UserMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication//(exclude = {DataSourceAutoConfiguration.class })
@EnableMongoRepositories("com.quizmaster.repositories") //to activate MongoDB repositories
@ComponentScan("com.quizmaster.repositories")
public class QuizmasterApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(QuizmasterApplication.class, args);
    }

    @Autowired
    private UserMongoRepository userMongoRepository;

    @Override
    public void run(String... args) throws Exception {
        User a = new User("Asd");

        userMongoRepository.save(a);

    }
}
