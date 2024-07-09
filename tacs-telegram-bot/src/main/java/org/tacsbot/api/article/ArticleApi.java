package org.tacsbot.api.article;

import org.apache.http.HttpException;
import org.tacsbot.exceptions.UnauthorizedException;
import org.tacsbot.model.Annotation;
import org.tacsbot.model.Article;
import org.tacsbot.model.UserSession;

import java.io.IOException;
import java.util.List;

public interface ArticleApi {

    String createArticle(Article article, UserSession userSession) throws IllegalArgumentException, HttpException, IOException, UnauthorizedException;

    List<Article> getAllArticles() throws HttpException, UnauthorizedException;

    List<Article> getArticlesOf(UserSession userSession) throws IllegalArgumentException, HttpException, UnauthorizedException;

    boolean suscribeToArticle(Article article, UserSession userSession) throws IllegalArgumentException, HttpException, UnauthorizedException;

    Article closeArticle(Article article, UserSession userSession) throws HttpException, IllegalArgumentException, UnauthorizedException;

    List<Annotation> viewArticleSubscriptions(Article article) throws HttpException;

}
