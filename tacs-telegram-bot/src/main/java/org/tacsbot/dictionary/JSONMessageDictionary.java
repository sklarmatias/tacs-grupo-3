package org.tacsbot.dictionary;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.tacsbot.model.Article;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
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

    public SimpleDateFormat getDateFormat(String languageCode){
        return (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.DATE_FIELD, Locale.forLanguageTag(languageCode));
    }

    public SimpleDateFormat getTimeFormat(String languageCode){
        return (SimpleDateFormat) DateFormat.getTimeInstance(DateFormat.DEFAULT, Locale.forLanguageTag(languageCode));
    }

    private String parseArticleToStringWithGivenTemplate(Article article, String languageCode, String template){
        SimpleDateFormat dateFormat = getDateFormat(languageCode);
        SimpleDateFormat timeFormat = getTimeFormat(languageCode);
        NumberFormat nf = NumberFormat.getInstance(Locale.forLanguageTag(languageCode));
        nf.setMinimumFractionDigits(2);
        return String.format(template,
                article.getStatus(),
                article.getName(),
                article.getImage(),
                article.getLink(),
                article.getUserGets(),
                "$" + nf.format(article.getCost()),
                article.getUsersMax(),
                article.getUsersMin(),
                dateFormat.format(article.getDeadline()),
                String.format("%s %s", dateFormat.format(article.getCreationDate()), timeFormat.format(article.getCreationDate())),
                article.getAnnotationsCounter());
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
}
