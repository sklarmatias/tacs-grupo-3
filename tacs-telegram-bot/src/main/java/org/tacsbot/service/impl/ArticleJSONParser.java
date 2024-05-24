package org.tacsbot.service.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.tacsbot.model.Article;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

public class ArticleJSONParser {

    public static String parseArticleToJSON(Article article) throws IOException {
        ObjectWriter objectMapper = new ObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd"))
                .writer()
                .withDefaultPrettyPrinter();
        try {
            return objectMapper.writeValueAsString(article);
        } catch (JsonProcessingException e) {
            System.out.printf("[Error] Cannot parse article:\n%s\n%s\n", article.getDetailedString(), e.getMessage());
            throw new IOException();
        }
    }

    public static Article parseJSONToArticle(String json){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        df.setTimeZone(TimeZone.getDefault());
        ObjectMapper mapper = new ObjectMapper()
                .setDateFormat(df);
            try {
            return mapper.readValue(json, Article.class);
        } catch (JsonProcessingException e) {
            System.out.printf("[Error] Cannot process JSON:\n%s\nException msg:\n%s\n",
                    json,
                    e.getMessage());
            e.printStackTrace();
            throw new IllegalArgumentException("JSON with the wrong format.");
        }
    }

    public static List<Article> parseJSONToArticleList(String json){
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            System.out.printf("[Error] Cannot process JSON:\n%s\nException msg:\n%s\n",
                    json,
                    e.getMessage());
            e.printStackTrace();
            throw new IllegalArgumentException("JSON with the wrong format.");
        }
    }

}
