package com.quizmaster.backend;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource("classpath:application-test.properties")
@AutoConfigureMockMvc
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
//    @Test
//    public void shouldGetQuizById() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders
//                .get("/quizzes/5f8ff6b3139bc460b131fa9b"))
//                .andExpect(status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());
//    }
//
//    @Test
//    public void shouldFailToGetQuizById() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders
//                .get("/quizzes/9999"))
//                .andExpect(status().isNotFound());
//    }
    @Test
    public void shouldFailBecauseOfSecurityMeasures() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/quizzes/9999"))
                .andExpect(status().isUnauthorized());
    }

    /* Test for createdAt date change should look like this
    @Test
    public void shouldGetQuizById() throws Exception {
        Quiz oldQuiz = quizMongoRepository.getById("5f918f6b894d6016707a019f");
        LocalDateTime initialcreatedAt = oldQuiz.getcreatedAt();

        oldQuiz.setcreatedAt(LocalDateTime.now());

        mockMvc.perform(MockMvcRequestBuilders
                .put("/quizzes/5f918f6b894d6016707a019f", "5f918f6b894d6016707a019f")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonToString(oldQuiz))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertThat(quizMongoRepository.getById("5f918f6b894d6016707a019f").getcreatedAt()).isEqualTo(initialcreatedAt);
    }*/


}
