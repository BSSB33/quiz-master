package com.quizmaster.backend.entities;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.quizmaster.backend.services.QuestionDeserializer;
import lombok.Data;
import org.apache.tomcat.jni.Local;

import java.time.LocalDateTime;
import java.util.List;


@JsonDeserialize(using = QuestionDeserializer.class)
@Data
public class PlayerScore {
    String nickname;
    LocalDateTime connectAt;
    List<Boolean> answers;
    String sessionID;

    public PlayerScore(String ID, LocalDateTime created){ this.sessionID = ID; this.connectAt = created;}

    public void setNickname(String nickname) { this.nickname = nickname; }

    public void addAnswer(boolean add){ this.answers.add(add);}

}
