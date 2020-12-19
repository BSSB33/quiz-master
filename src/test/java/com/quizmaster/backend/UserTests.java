package com.quizmaster.backend;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.quizmaster.backend.entities.User;
import com.quizmaster.backend.repositories.UserMongoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.assertj.core.api.Assertions.assertThat;
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
    private UserMongoRepository userMongoRepository;

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


    User testUser = new User("UserMadeForTestPurposesForQuizMaster@gmail.com", "FakeGoogleIdForTestPurposes");

    @Test
    public void shouldFailToGetUserById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/users/9999"))
                .andExpect(status().isNotFound());

    }

    @Test
    public void addNewUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .content(jsonToString(testUser))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        userMongoRepository.findAll().forEach(qu ->
        {
            if (qu.getGoogleId().equals(testUser.getGoogleId()) && qu.getEmail().equals(testUser.getEmail())) {
                assertThat(userMongoRepository.getById(qu.getId()).getEmail().equals("UserMadeForTestPurposesForQuizMaster@gmail.com"));
                userMongoRepository.deleteById(qu.getId());

            }
        });
    }

    @Test
    public void deleteNewUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .content(jsonToString(testUser))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        userMongoRepository.findAll().forEach(qu ->
        {
            if (qu.getGoogleId().equals(testUser.getGoogleId()) && qu.getEmail().equals(testUser.getEmail())) {
                userMongoRepository.deleteById(qu.getId());
                assertThat(userMongoRepository.getById(qu.getId()) == null);
            }
        });
    }

    @Test
    public void shouldFailWithNoGoogleId() throws Exception {
        User noGoogleId = new User("qjkwebni@gmail.com", null);
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .content(jsonToString(noGoogleId))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldFailWithEmail() throws Exception {
        User noEmailId = new User(null, "UltraFakeGoogleId");
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .content(jsonToString(noEmailId))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldFailWithEmailPut() throws Exception {
        User noEmailId = new User(null, "UltraFakeGoogleId");
        mockMvc.perform(MockMvcRequestBuilders.put("/users/5fbe9974b9deee6a70f5649f")
                .content(jsonToString(noEmailId))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldFailWithGoogleIdPut() throws Exception {
        User noGoogleId = new User("UltraFakeEmail@gmail.com", null);
        mockMvc.perform(MockMvcRequestBuilders.put("/users/5fbe9974b9deee6a70f5649f")
                .content(jsonToString(noGoogleId))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldGetUserById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/users/5fbe9974b9deee6a70f5649f"))
                .andExpect(status().isOk());
    }


}
