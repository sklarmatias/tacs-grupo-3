package org.tacsbot.cache;

import org.tacsbot.handlers.impl.ArticleCreationHandler;
import org.tacsbot.model.Article;
import org.tacsbot.model.User;
import org.tacsbot.model.UserSession;

import java.io.IOException;
import java.util.List;

public interface CacheService {

    void saveArticles(Long chatId, List<Article> articles);

    List<Article> getArticles(Long chatId);

    void deleteChatId(Long chatId);

    void saveArticleCreationHandler(Long chatId, ArticleCreationHandler articleCreationHandler);

    ArticleCreationHandler getArticleCreationHandler(Long chatId);

    UserSession getSession(Long chatId);

    void saveSession(Long chatId, UserSession userSession) throws IOException;

    int deleteSession(Long chatId);

    Long getChatIdOfSession(UserSession userSession);

    void saveChatIdOfSession(UserSession userSession, Long chatId);

    void deleteChatIdOfSession(UserSession userSession);

    void addSessionMapping(Long chatId, UserSession userSession) throws IOException;

    void deleteSessionMapping(Long chatId, UserSession userSession);

    void closeConnection();

}
