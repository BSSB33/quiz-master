package com.quizmaster.backend;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.quizmaster.backend.entities.Model;
import com.quizmaster.backend.entities.MultipleChoicesModel;
import com.quizmaster.backend.entities.Question;
import com.quizmaster.backend.entities.Quiz;
import com.quizmaster.backend.repositories.QuizMongoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@TestPropertySource("classpath:application-test.properties")
@SpringBootTest

public class UserTests {


    @Autowired
    private Environment environment;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private QuizMongoRepository quizMongoRepository;
    @Test
    public void shouldFailToGetUserById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/users/9999"))
                .andExpect(status().isNotFound());
    }


}
