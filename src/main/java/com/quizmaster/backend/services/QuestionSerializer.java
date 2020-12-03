package com.quizmaster.backend.services;

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
