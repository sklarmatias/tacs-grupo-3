package org.tacsbot.dictionary;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.tacsbot.model.Annotation;
import org.tacsbot.model.Article;
import org.tacsbot.model.ArticleStatus;
import org.tacsbot.model.CostType;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class JSONMessageDictionary implements MessageDictionary{

    @Override
    public String getMessage(String message, String languageCode) {
        String msg = getJSONNode(languageCode).path(message).asText();
        if (Objects.equals(msg, "") || msg == null)
            return getJSONNode(languageCode).path(message).asText();
        return msg;
    }

    @Override
    public String annotationToString(Annotation annotation, String languageCode) {
        return String.format("(%s)\n%s, %s (%s)",
                parseDateTimeLocally(annotation.getDate(), languageCode),
                annotation.getUser().getSurname(),
                annotation.getUser().getName(),
                annotation.getUser().getEmail());
    }

    @Override
    public String annotationListToString(List<Annotation> annotationList, String languageCode) {
        StringBuilder s = new StringBuilder();
        for (Annotation annotation: annotationList){
            s.append(annotationToString(annotation, languageCode)).append("\n");
        }
        return s.toString();
    }

    @Override
    public String articleToString(Article article, String languageCode) {
        return parseArticleToStringWithGivenTemplate(article, languageCode, getMessage("ARTICLE_STRING", languageCode));
    }

    @Override
    public String articleListToString(List<Article> articleList, String languageCode) {
        String template = getMessage("ARTICLE_STRING", languageCode);
        StringBuilder s = new StringBuilder();
        int i = 1;
        for (Article article: articleList){
            s.append(i).append(":\n");
            s.append(parseArticleToStringWithGivenTemplate(article, languageCode, template)).append("\n");
            i+=1;
        }
        return s.toString();
    }

    // utils

    private JsonNode getJSONNode(String languageCode){
        try {
            File file = new File(Objects.requireNonNull(getClass().getResource("/messages/" + languageCode + ".json")).toURI());
            return new ObjectMapper().readTree(file);
        } catch (IOException | URISyntaxException | NullPointerException e) {
            throw new RuntimeException(e);
        }
    }

    private String parseArticleToStringWithGivenTemplate(Article article, String languageCode, String template){
        SimpleDateFormat dateFormat = getDateFormat(languageCode);
        return String.format(template,
                parseLocally(article.getStatus(), languageCode),
                article.getName(),
                article.getImage(),
                article.getLink(),
                article.getUserGets(),
                "$" + parseLocally(article.getCost(), languageCode),
                parseLocally(article.getCostType(), languageCode),
                article.getUsersMax(),
                article.getUsersMin(),
                dateFormat.format(article.getDeadline()),
                parseDateTimeLocally(article.getCreationDate(), languageCode),
                article.getAnnotationsCounter());
    }

    // commons

    private String parseLocally(Double number, String languageCode){
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.forLanguageTag(languageCode));
        numberFormat.setMinimumFractionDigits(2);
        return numberFormat.format(number);
    }
    private String parseLocally(ArticleStatus status, String languageCode){
        return getJSONNode(languageCode).path("ARTICLES").path("ARTICLE_STATUS").path(status.toString()).asText();
    }

    private String parseLocally(CostType status, String languageCode){
        return getJSONNode(languageCode).path("ARTICLES").path("COST_TYPE").path(status.toString()).asText();
    }

    private String parseDateTimeLocally(Date date, String languageCode){
        return String.format("%s %s",
                getDateFormat(languageCode).format(date),
                getTimeFormat(languageCode).format(date));
    }

    private SimpleDateFormat getDateFormat(String languageCode){
        return (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.DATE_FIELD, Locale.forLanguageTag(languageCode));
    }

    private SimpleDateFormat getTimeFormat(String languageCode){
        return (SimpleDateFormat) DateFormat.getTimeInstance(DateFormat.DEFAULT, Locale.forLanguageTag(languageCode));
    }

}
