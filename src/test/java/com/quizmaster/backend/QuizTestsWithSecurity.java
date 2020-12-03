package com.quizmaster.backend;


import com.quizmaster.backend.repositories.QuizMongoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@TestPropertySource("classpath:application.properties")
@SpringBootTest
//Tests to be run with security on
public class QuizTestsWithSecurity {


    @Autowired
    private MockMvc mockMvc;



    @Test
    public void shouldFailBecauseOfSecurityMeasures() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/quizzes/9999"))
                .andExpect(status().isUnauthorized());
    }
}
