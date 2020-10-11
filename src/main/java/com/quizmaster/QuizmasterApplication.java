package com.quizmaster;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication//(exclude = {DataSourceAutoConfiguration.class })
public class QuizmasterApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuizmasterApplication.class, args);
    }

}
