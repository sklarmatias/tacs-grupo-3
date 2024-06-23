package org.tacsbot.parser.article.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.Setter;
import org.tacsbot.model.Article;
import org.tacsbot.parser.article.ArticleParser;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

public class ArticleJSONParser implements ArticleParser {
    @Setter
    ObjectMapper objectMapper = new ObjectMapper();
    public String parseArticleToJSON(Article article) throws IOException {
        ObjectWriter objectWriter = objectMapper
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd"))
                .writer()
                .withDefaultPrettyPrinter();
        try {
            return objectWriter.writeValueAsString(article);
        } catch (JsonProcessingException e) {
            System.err.printf("[Error] Cannot parse article:\n%s\n%s\n", article.getDetailedString(), e.getMessage());
            throw new IOException();
        }
    }

    public Article parseJSONToArticle(String json){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        df.setTimeZone(TimeZone.getDefault());
        objectMapper.setDateFormat(df);
            try {
            return objectMapper.readValue(json, Article.class);
        } catch (JsonProcessingException e) {
            System.err.printf("[Error] Cannot process JSON:\n%s\nException msg:\n%s\n",
                    json,
                    e.getMessage());
            e.printStackTrace();
            throw new IllegalArgumentException("JSON with the wrong format.");
        }
    }

    public List<Article> parseJSONToArticleList(String json){
        try {
            return objectMapper.readValue(json, new TypeReference<>() {
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
