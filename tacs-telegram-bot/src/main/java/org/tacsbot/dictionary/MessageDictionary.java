package org.tacsbot.dictionary;

import org.tacsbot.model.Article;

import java.util.List;

public interface MessageDictionary {

    String getMessage(String message, String languageCode);

    String articleToString(Article article, String languageCode);

    String articleListToString(List<Article> articleList, String languageCode);

}
