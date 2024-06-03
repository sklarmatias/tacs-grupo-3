package org.tacsbot.dictionary;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;

public class JSONMessageDictionary implements MessageDictionary{

    private JsonNode getJSONNode(String language){
                try {
            File file = new File(getClass().getResource("/messages/" + language + ".json").toURI());
            return new ObjectMapper().readTree(file);
        } catch (IOException | URISyntaxException | NullPointerException e) {
            throw new RuntimeException(e);
        }
    }

    private String parseLangCode(String langCode){
        return Objects.equals(langCode, "en") ? "english": "spanish";
    }

    @Override
    public String getMessage(String message, String language) {
        String msg = getJSONNode(parseLangCode(language)).path(message).asText();
        if (Objects.equals(msg, "") || msg == null)
            return getJSONNode(parseLangCode("spanish")).path(message).asText();
        return msg;
    }

    @Override
    public String getMessage(String message) {
        return getMessage(message, "en");
    }
}
