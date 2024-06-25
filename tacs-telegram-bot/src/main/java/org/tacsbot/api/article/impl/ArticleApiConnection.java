package org.tacsbot.api.article.impl;

import lombok.Getter;
import lombok.Setter;
import org.apache.http.HttpException;
import org.tacsbot.api.article.ArticleApi;
import org.tacsbot.model.Annotation;
import org.tacsbot.model.Article;
import org.tacsbot.parser.annotation.AnnotationParser;
import org.tacsbot.parser.annotation.impl.AnnotationJSONParser;
import org.tacsbot.parser.article.ArticleParser;
import org.tacsbot.parser.article.impl.ArticleJSONParser;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.util.List;

public class ArticleApiConnection implements ArticleApi {

    @Setter
    private ArticleHttpConnector articleHttpConnector;
    private ArticleParser articleJSONParser;
    private AnnotationParser annotationParser;

    public ArticleApiConnection(){
        articleHttpConnector = new ArticleHttpConnector();
        articleJSONParser = new ArticleJSONParser();
        annotationParser = new AnnotationJSONParser();
    }

    @Override
    public String createArticle(Article article) throws IllegalArgumentException, HttpException {
        try {
            String JSONArticle = articleJSONParser.parseArticleToJSON(article);
            HttpResponse<String> response = articleHttpConnector.createArticleConnector(JSONArticle, article.getOwner());
            if (response.statusCode() == 201)
                return response.body();
            else {
                System.err.printf("Response code %d creating the following Article:\n%s\nResponse body: %s",
                        response.statusCode(),
                        JSONArticle,
                        response.body());
                throw new IllegalArgumentException("Couldn't create article.");
            }
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new HttpException(String.format("[Error] Sending the following article:\n%s\n%s\n",
                    article, e.getMessage()));
        }
    }

    @Override
    public List<Article> getAllArticles() throws HttpException {
        return getArticlesOf(null);
    }

    @Override
    public List<Article> getArticlesOf(String ownerId) throws IllegalArgumentException, HttpException {
        try {
            HttpResponse<String> response = articleHttpConnector.getArticles(ownerId);
            if (response.statusCode() == 200)
                return articleJSONParser.parseJSONToArticleList(response.body());
            else{
                System.err.printf("Status code %d requesting all articles of %s.\nResponse body:\n%s\n",
                        response.statusCode(),
                        ownerId,
                        response.body());
                throw new IllegalArgumentException(response.body());
            }
        } catch (URISyntaxException | IOException | InterruptedException e) {
            System.err.printf("Exception getting all articles\nownerId = %s\n", ownerId);
            throw new HttpException(String.format("[Error] Exception getting all articles\nownerId = %s\n%s\n",
                    ownerId, e.getMessage()));
        }
    }

    @Override
    public boolean suscribeToArticle(Article article, String userId) throws HttpException, IllegalArgumentException {
        try{
            HttpResponse<String> response = articleHttpConnector.suscribeToArticle(article.getId(), userId);
            return response.statusCode() == 200;
        } catch (URISyntaxException | InterruptedException | IOException e) {
            throw new HttpException(String.format("[Error] Exception subscribing userId %s to articleId %s.\n%s\n",
                    userId, article.getId(), e.getMessage()));
        }
    }

    @Override
    public Article closeArticle(Article article, String userId) throws HttpException, IllegalArgumentException {
        try{
            HttpResponse<String> response = articleHttpConnector.closeArticle(article.getId(), userId);
            if (response.statusCode() == 200){
                return articleJSONParser.parseJSONToArticle(response.body());
            } else{
                throw new IllegalArgumentException("Couldn't close article of id " + article.getId());
            }
        } catch (URISyntaxException | InterruptedException | IOException e) {
            throw new HttpException(String.format("[Error] Exception subscribing userId %s to articleId %s.\n%s\n",
                    userId, article.getId(), e.getMessage()));
        }
    }

    @Override
    public List<Annotation> viewArticleSubscriptions(Article article) throws HttpException {
        try{
            HttpResponse<String> response = articleHttpConnector.getSubscriptions(article.getId());
            if (response.statusCode() == 200){
                return annotationParser.parseJSONToAnnotation(response.body());
            } else{
                throw new IllegalArgumentException(
                        String.format("Couldn't get subscriptions of article of id %s\n%d: %s",article.getId(), response.statusCode(), response.body()));
            }
        } catch (URISyntaxException | InterruptedException | IOException e) {
            throw new HttpException(String.format("[Error] Exception getting subscriptions of articleId %s.\n%s\n",
                    article.getId(), e.getMessage()));
        }
    }

}
