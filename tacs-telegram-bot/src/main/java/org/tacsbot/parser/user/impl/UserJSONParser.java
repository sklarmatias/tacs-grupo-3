package org.tacsbot.parser.user.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.Setter;
import org.tacsbot.model.User;
import org.tacsbot.parser.user.UserParser;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class UserJSONParser implements UserParser {
    @Setter
    ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public String parseUserToJSON(User user) throws IOException {
        ObjectWriter objectWriter = objectMapper
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd"))
                .writer()
                .withDefaultPrettyPrinter();
        try {
            return objectWriter.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            System.err.printf("[Error] Cannot parse user:\n%s\n%s\n", user, e.getMessage());
            throw new IOException();
        }
    }

    @Override
    public User parseJSONToUser(String json) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        df.setTimeZone(TimeZone.getDefault());
        objectMapper.setDateFormat(df);
        try {
            return objectMapper.readValue(json, User.class);
        } catch (JsonProcessingException e) {
            System.err.printf("[Error] Cannot process JSON:\n%s\nException msg:\n%s\n",
                    json,
                    e.getMessage());
            e.printStackTrace();
            throw new IllegalArgumentException("JSON with the wrong format.");
        }
    }
}
