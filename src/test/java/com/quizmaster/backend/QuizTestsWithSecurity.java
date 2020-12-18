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
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@TestPropertySource("classpath:application.properties")
@SpringBootTest
//Tests to be run with security on
public class QuizTestsWithSecurity {

    private String workingQuizID = "5fdcdd4f2e342f6f6b014386";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private QuizMongoRepository quizMongoRepository;

    LocalDateTime random = LocalDateTime.of(2020, Month.NOVEMBER, 29, 20, 00, 00);
    Model m1 = new MultipleChoicesModel("Which one is Letter C?", List.of("A", "B", "C", "D"), List.of(3));
    Model m2 = new MultipleChoicesModel("Which one is Letter A?", List.of("A", "B", "C", "D"), List.of(1));
    Question q1 = new Question("qm.multiple_choice", m1);
    Question q2 = new Question("qm.multiple_choice", m2);

    private String jsonToString(final Object obj) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.registerModule(new ParameterNamesModule());
            objectMapper.registerModule(new Jdk8Module());

            objectMapper.disable(MapperFeature.USE_ANNOTATIONS);
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void shouldFailBecauseOfSecurityMeasures() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/quizzes/9999"))
                .andExpect(status().isUnauthorized());
    }

    //    These test need to be run without security
    @Test
    public void shouldGetQuizById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/quizzes/" + workingQuizID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldFailToGetQuizById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/quizzes/9999"))
                .andExpect(status().isUnauthorized());
    }


    //Test for createdAt date change should look like this
    @Test
    public void shouldKeepCreatedAt() throws Exception {

        Quiz quiz = new Quiz("ShouldKeepCreatedAt", "d", LocalDateTime.now().plusMinutes(2), "Random Note", List.of(q1, q2));

        Quiz oldQuiz = quizMongoRepository.getById(workingQuizID);
        LocalDateTime initialCreatedAt = oldQuiz.getCreatedAt();

        mockMvc.perform(MockMvcRequestBuilders.put("/quizzes/" + workingQuizID)
                .content(jsonToString(quiz))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        assertThat(quizMongoRepository.getById(workingQuizID).getCreatedAt()).isEqualTo(initialCreatedAt);
    }

    @Test
    public void shouldBeForbiddenNullTitle() throws Exception {

        Quiz quiz = new Quiz(null, "test", LocalDateTime.now().plusMinutes(2), "Random Note", List.of(q1, q2));

        mockMvc.perform(MockMvcRequestBuilders.put("/quizzes/5f92fc1dc14fbe24614fcd0d")
                .content(jsonToString(quiz))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldBeForbiddenNullDescription() throws Exception {

        Quiz quiz = new Quiz("test", null, LocalDateTime.now().plusMinutes(2), "Random Note", List.of(q1, q2));

        mockMvc.perform(MockMvcRequestBuilders.put("/quizzes/5f92fc1dc14fbe24614fcd0d")
                .content(jsonToString(quiz))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldBeForbiddenNullDate() throws Exception {

        Quiz quiz = new Quiz("test", "test", null, "Random Note", List.of(q1, q2));

        mockMvc.perform(MockMvcRequestBuilders.put("/quizzes/5f92fc1dc14fbe24614fcd0d")
                .content(jsonToString(quiz))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldBeForbiddenNullNote() throws Exception {

        Quiz quiz = new Quiz("test", "test", LocalDateTime.now().plusMinutes(2), null, List.of(q1, q2));

        mockMvc.perform(MockMvcRequestBuilders.put("/quizzes/5f92fc1dc14fbe24614fcd0d")
                .content(jsonToString(quiz))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldBeForbiddenNullQuestion() throws Exception {

        Quiz quiz = new Quiz("test", "test", LocalDateTime.now().plusMinutes(2), "Random Note", null);

        mockMvc.perform(MockMvcRequestBuilders.put("/quizzes/5f92fc1dc14fbe24614fcd0d")
                .content(jsonToString(quiz))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldGetRightQuestionAfterChange() throws Exception {

        Quiz quiz = new Quiz("ShouldKeepCreatedAt", "d", LocalDateTime.now().plusMinutes(2), "Random Note", List.of(q1, q2));

        Quiz oldQuiz = quizMongoRepository.getById(workingQuizID);

        mockMvc.perform(MockMvcRequestBuilders.put("/quizzes/" + workingQuizID)
                .content(jsonToString(quiz))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        assertThat(quizMongoRepository.getById(workingQuizID).getQuestions()).isEqualTo(List.of(q1, q2));
    }

    @Test
    public void shouldGetRightDescriptionAfterChange() throws Exception {

        Quiz quiz = new Quiz("ShouldKeepCreatedAt", "change of description", LocalDateTime.now().plusMinutes(2), "Random Note", List.of(q1, q2));

        Quiz oldQuiz = quizMongoRepository.getById(workingQuizID);

        mockMvc.perform(MockMvcRequestBuilders.put("/quizzes/" + workingQuizID)
                .content(jsonToString(quiz))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        assertThat(quizMongoRepository.getById(workingQuizID).getDescription().equals("change of description"));
    }

    @Test
    public void shouldGetRightNameAfterChange() throws Exception {

        Quiz quiz = new Quiz("change of name", "description", LocalDateTime.now().plusMinutes(2), "Random Note", List.of(q1, q2));

        Quiz oldQuiz = quizMongoRepository.getById(workingQuizID);

        mockMvc.perform(MockMvcRequestBuilders.put("/quizzes/" + workingQuizID)
                .content(jsonToString(quiz))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        assertThat(quizMongoRepository.getById(workingQuizID).getDescription().equals("change of name"));
    }

    @Test
    public void shouldGetRightNoteAfterChange() throws Exception {

        Quiz quiz = new Quiz("name", "description", LocalDateTime.now().plusMinutes(2), "change of random note", List.of(q1, q2));

        Quiz oldQuiz = quizMongoRepository.getById(workingQuizID);

        mockMvc.perform(MockMvcRequestBuilders.put("/quizzes/" + workingQuizID)
                .content(jsonToString(quiz))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        assertThat(quizMongoRepository.getById(workingQuizID).getNotes().equals("change of random note"));
    }

    @Test
    public void deleteQuiz() throws Exception {

        Quiz quiz = new Quiz("HelloWorldForTestPurposesAndThatNeedsToBeLongEnoughForNoMisinterpretation", "test description", LocalDateTime.now().plusMinutes(2), "test Note", List.of(q1, q2));

        mockMvc.perform(MockMvcRequestBuilders.post("/quizzes")
                .content(jsonToString(quiz))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        quizMongoRepository.findAll().forEach(qu ->
        {
            if (qu.getTitle().equals(quiz.getTitle()) && qu.getQuestions().equals(quiz.getQuestions())) {
                quizMongoRepository.deleteById(qu.getId());
                assertThat(quizMongoRepository.getById(qu.getId()) == null);
            }
        });


    }

    @Test
    public void CreateNewQuiz() throws Exception {

        Quiz quiz = new Quiz("HelloWorldForTestPurposesAndThatNeedsToBeLongEnoughForNoMisinterpretation", "test description", LocalDateTime.now().plusMinutes(2), "test Note", List.of(q1, q2));

        mockMvc.perform(MockMvcRequestBuilders.post("/quizzes")
                .content(jsonToString(quiz))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        quizMongoRepository.findAll().forEach(qu ->
        {
            if (qu.getTitle().equals(quiz.getTitle()) && qu.getQuestions().equals(quiz.getQuestions())) {
                assertThat(quizMongoRepository.getById(qu.getId()).getTitle().equals("HelloWorldForTestPurposesAndThatNeedsToBeLongEnoughForNoMisinterpretation"));
                quizMongoRepository.deleteById(qu.getId());

            }
        });


    }

    @Test
    public void deleteNonExistentQuiz() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                .delete("/quizzes/123123123"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldFailChangeWithWrongId() throws Exception {

        Quiz quiz = new Quiz("ShouldKeepCreatedAt", "d", LocalDateTime.now().plusMinutes(2), "Random Note", List.of(q1, q2));

        mockMvc.perform(MockMvcRequestBuilders.put("/quizzes/123123123123")
                .content(jsonToString(quiz))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

    }

    @Test
    public void shouldFailPostEmptyTitle() throws Exception {

        Quiz quiz = new Quiz(null, "test description", LocalDateTime.now().plusMinutes(2), "test Note", List.of(q1, q2));

        mockMvc.perform(MockMvcRequestBuilders.post("/quizzes")
                .content(jsonToString(quiz))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldFailPostEmptyDescription() throws Exception {

        Quiz quiz = new Quiz("HelloWorldForTestPurposesAndThatNeedsToBeLongEnoughForNoMisinterpretation", null, LocalDateTime.now().plusMinutes(2), "test Note", List.of(q1, q2));

        mockMvc.perform(MockMvcRequestBuilders.post("/quizzes")
                .content(jsonToString(quiz))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldFailPostEmptyStartingTime() throws Exception {

        Quiz quiz = new Quiz("HelloWorldForTestPurposesAndThatNeedsToBeLongEnoughForNoMisinterpretation", "test description", null, "test Note", List.of(q1, q2));

        mockMvc.perform(MockMvcRequestBuilders.post("/quizzes")
                .content(jsonToString(quiz))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldFailPostEmptyNotes() throws Exception {

        Quiz quiz = new Quiz("HelloWorldForTestPurposesAndThatNeedsToBeLongEnoughForNoMisinterpretation", "test description", LocalDateTime.now().plusMinutes(2), null, List.of(q1, q2));

        mockMvc.perform(MockMvcRequestBuilders.post("/quizzes")
                .content(jsonToString(quiz))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
