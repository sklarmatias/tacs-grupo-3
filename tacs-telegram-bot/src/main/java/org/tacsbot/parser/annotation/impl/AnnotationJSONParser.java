package org.tacsbot.parser.annotation.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.tacsbot.model.Annotation;
import org.tacsbot.parser.annotation.AnnotationParser;
import java.util.List;

public class AnnotationJSONParser implements AnnotationParser {


    @Override
    public List<Annotation> parseJSONToAnnotation(String json) {
        ObjectMapper mapper = new ObjectMapper();
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

}
