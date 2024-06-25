package org.tacsbot.parser.annotation.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.tacsbot.model.Annotation;
import org.tacsbot.parser.annotation.AnnotationParser;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class AnnotationJSONParser implements AnnotationParser {

    ObjectMapper mapper = new ObjectMapper();
    @Override
    public List<Annotation> parseJSONToAnnotation(String json) {

        mapper.setTimeZone(TimeZone.getDefault());
        try {
            return mapper.readValue(json, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            System.err.printf("[Error] Cannot process JSON:\n%s\nException msg:\n%s\n",
                    json,
                    e.getMessage());
            e.printStackTrace();
            throw new IllegalArgumentException("JSON with the wrong format.");
        }
    }
    public String parseAnnotationToJSON(Annotation annotation) throws IOException {
        ObjectWriter objectWriter = mapper
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
                .writer()
                .withDefaultPrettyPrinter();
        try {
            return objectWriter.writeValueAsString(annotation);
        } catch (JsonProcessingException e) {
            System.err.printf("[Error] Cannot parse annotation:\n%s\n%s\n", annotation.toString(), e.getMessage());
            throw new IOException();
        }
    }
    @Override
    public String parseAnnotationsToJSON(List<Annotation> annotations) {
        List<String> jsonAnnotations = new ArrayList<>();
        for (Annotation annotation : annotations) {
            try {
                String jsonAnnotation = parseAnnotationToJSON(annotation);
                jsonAnnotations.add(jsonAnnotation);
            } catch (IOException e) {
                System.err.printf("[Error] Cannot parse annotation to JSON: %s\n", annotation.toString());
                // Optionally, handle the exception or rethrow it
            }
        }
        return "[" + String.join(",", jsonAnnotations) + "]";
    }

}
