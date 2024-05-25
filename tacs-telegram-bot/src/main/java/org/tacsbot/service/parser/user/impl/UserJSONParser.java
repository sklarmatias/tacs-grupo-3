package org.tacsbot.service.parser.user.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.tacsbot.model.User;
import org.tacsbot.service.parser.user.UserParser;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class UserJSONParser implements UserParser {
    @Override
    public String parsUserToJSON(User user) throws IOException {
        ObjectWriter objectMapper = new ObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd"))
                .writer()
                .withDefaultPrettyPrinter();
        try {
            return objectMapper.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            System.out.printf("[Error] Cannot parse user:\n%s\n%s\n", user, e.getMessage());
            throw new IOException();
        }
    }

    @Override
    public User parseJSONToUser(String json) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        df.setTimeZone(TimeZone.getDefault());
        ObjectMapper mapper = new ObjectMapper()
                .setDateFormat(df);
        try {
            return mapper.readValue(json, User.class);
        } catch (JsonProcessingException e) {
            System.out.printf("[Error] Cannot process JSON:\n%s\nException msg:\n%s\n",
                    json,
                    e.getMessage());
            e.printStackTrace();
            throw new IllegalArgumentException("JSON with the wrong format.");
        }
    }
}
