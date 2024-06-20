package org.tacsbot.cache;

import org.tacsbot.handlers.impl.ArticleCreationHandler;
import org.tacsbot.model.Article;
import org.tacsbot.model.User;

import java.io.IOException;
import java.util.List;

public interface CacheService {

    void saveArticles(Long chatId, List<Article> articles);

    List<Article> getArticles(Long chatId);

    void deleteChatId(Long chatId);

    void saveArticleCreationHandler(Long chatId, ArticleCreationHandler articleCreationHandler);

    ArticleCreationHandler getArticleCreationHandler(Long chatId);

    User getUser(Long chatId);

    void saveUser(Long chatId, User user) throws IOException;

    int deleteUser(Long chatId);

    Long getChatIdOfUser(String userId);

    void saveChatIdOfUser(String userId, Long chatId);

    void deleteChatIdOfUser(String userId);

    void addUserMapping(Long chatId, User user) throws IOException;

    void deleteUserMapping(Long chatId, User user);

    void closeConnection();

}
