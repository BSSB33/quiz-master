package com.quizmaster.backend;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
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

@TestPropertySource("classpath:application-test.properties")
@AutoConfigureMockMvc
@TestPropertySource("classpath:application-test.properties")
@SpringBootTest
public class QuizTests {

    @Autowired
    private Environment environment;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private QuizMongoRepository quizMongoRepository;

    private String jsonToString(final Object obj) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.disable(MapperFeature.USE_ANNOTATIONS);
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


//    These test need to be run without security
    @Test
    public void shouldGetQuizById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/quizzes/5f91921c894d6016707a01a0"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());
    }

    @Test
    public void shouldFailToGetQuizById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/quizzes/9999"))
                .andExpect(status().isNotFound());
    }
//    @Test
//    public void shouldFailBecauseOfSecurityMeasures() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders
//                .get("/quizzes/9999"))
//                .andExpect(status().isUnauthorized());
//    }

    //Test for createdAt date change should look like this
    @Test
    public void shouldKeepCreatedAt() throws Exception {


        LocalDateTime random = LocalDateTime.of(2020, Month.NOVEMBER, 29, 20, 00, 00);
        Model m1 = new MultipleChoicesModel("Which one is Letter C?", List.of("A", "B", "C", "D"), List.of(3));
        Model m2 = new MultipleChoicesModel("Which one is Letter A?", List.of("A", "B", "C", "D"), List.of(1));
        Question q1 = new Question("qm.multiple_choice", m1);
        Question q2 = new Question("qm.multiple_choice", m2);
        Quiz quiz = new Quiz("Testquiz", "d", LocalDateTime.now().plusMinutes(2), "Random Note", List.of(q1, q2));


        Quiz oldQuiz = quizMongoRepository.getById("5f91921c894d6016707a01a0");
        LocalDateTime initialCreatedAt = oldQuiz.getCreatedAt();

        oldQuiz.setCreatedAt(LocalDateTime.now());

        mockMvc.perform(MockMvcRequestBuilders.put("/quizzes/5f91921c894d6016707a01a0")
                .content(jsonToString(quiz))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertThat(quizMongoRepository.getById("5f91921c894d6016707a01a0").getCreatedAt()).isEqualTo(initialCreatedAt);
    }


}
