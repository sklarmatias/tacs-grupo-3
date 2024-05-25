package org.tacsbot.api.article;

import org.apache.http.HttpException;
import org.tacsbot.model.Article;

import java.io.IOException;
import java.util.List;

public interface ArticleApi {

    String createArticle(Article article) throws IllegalArgumentException, HttpException, IOException;

    List<Article> getAllArticles() throws HttpException;

    List<Article> getArticlesOf(String ownerId) throws IllegalArgumentException, HttpException;

    boolean suscribeToArticle(Article article, String userId) throws IllegalArgumentException, HttpException;

    void closeArticle(Article article);

    void viewArticleSubscriptions(Article article);

}
