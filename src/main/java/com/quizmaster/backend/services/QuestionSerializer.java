package com.quizmaster.backend.services;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.quizmaster.backend.entities.MultipleChoicesModel;
import com.quizmaster.backend.entities.Question;

import java.io.IOException;

//public class QuestionSerializer extends JsonSerializer<Question> {

//    @Override
//    public void serialize(Question value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
//        gen.writeStartObject();
//        gen.writeStringField("type", value.getType());
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        gen.writeFieldName("model");
//        gen.writeRawValue(objectMapper.writeValueAsString(value.getModel()));
//        gen.writeEndObject();
//    }
//}
