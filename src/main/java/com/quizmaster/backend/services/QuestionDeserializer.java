package com.quizmaster.backend.services;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quizmaster.backend.entities.Model;
import com.quizmaster.backend.entities.MultipleChoicesModel;
import com.quizmaster.backend.entities.Question;

import java.io.IOException;

public class QuestionDeserializer extends JsonDeserializer<Question> {

    @Override
    public Question deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        ObjectCodec oc = jsonParser.getCodec();
        JsonNode node = oc.readTree(jsonParser);
        String type = node.get("type").asText();

        ObjectMapper objectMapper = new ObjectMapper();
        Model model = null;

        if (type.equals("qm.multiple_choice")) {
            model = objectMapper.readValue(node.get("model").toString(), MultipleChoicesModel.class);
        }


        return new Question(type, model);
    }
}
