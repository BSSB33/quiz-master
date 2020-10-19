package com.quizmaster.backend.services;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.*;
import com.quizmaster.backend.entities.ImageQuestionModell;
import com.quizmaster.backend.entities.Model;
import com.quizmaster.backend.entities.MultipleChoicesModel;
import com.quizmaster.backend.entities.Question;

import java.io.IOException;

public class QuestionDeserializer extends JsonDeserializer<Question> {

    @Override
    public Question deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        //System.out.println("ASD");
        ObjectCodec oc = jsonParser.getCodec();
        JsonNode node = oc.readTree(jsonParser);
        String type = node.get("type").asText();

        ObjectMapper objectMapper = new ObjectMapper();
        Model model = null;

        if(type.equals("multiple")) {
            model = objectMapper.readValue(node.get("model").textValue(), MultipleChoicesModel.class);
            System.out.println("MODELL: " + model.toString());
        }

        //if(type.equals("image") && model instanceof ImageQuestionModell) this.model = model;


        return new Question(type, model);
    }
}
