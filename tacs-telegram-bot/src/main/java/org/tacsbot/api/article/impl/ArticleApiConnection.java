package org.tacsbot.api.article.impl;

import lombok.Setter;
import org.apache.http.HttpException;
import org.tacsbot.api.article.ArticleApi;
import org.tacsbot.api.utils.ApiHttpConnector;
import org.tacsbot.exceptions.UnauthorizedException;
import org.tacsbot.model.Annotation;
import org.tacsbot.model.Article;
import org.tacsbot.model.UserSession;
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
    private ApiHttpConnector apiHttpConnector;
    private ArticleParser articleJSONParser;
    private AnnotationParser annotationParser;

    public ArticleApiConnection(){
        apiHttpConnector = new ApiHttpConnector();
        articleJSONParser = new ArticleJSONParser();
        annotationParser = new AnnotationJSONParser();
    }

    @Override
    public String createArticle(Article article, UserSession userSession) throws IllegalArgumentException, HttpException, UnauthorizedException {
        try {
            String JSONArticle = articleJSONParser.parseArticleToJSON(article);
            HttpResponse<String> response = apiHttpConnector.post("/articles", JSONArticle, userSession.getSessionId());
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
    public List<Article> getAllArticles() throws HttpException, UnauthorizedException {
        try {
            HttpResponse<String> response = apiHttpConnector.get("/articles");
            if (response.statusCode() == 200)
                return articleJSONParser.parseJSONToArticleList(response.body());
            else{
                System.err.printf("Status code %d requesting all articles.\nResponse body:\n%s\n",
                        response.statusCode(),
                        response.body());
                throw new IllegalArgumentException(response.body());
            }
        } catch (URISyntaxException | IOException | InterruptedException e) {
            System.err.println("Exception getting all articles");
            throw new HttpException(String.format("[Error] Exception getting all articles\n%s\n", e.getMessage()));
        }
    }

    @Override
    public List<Article> getArticlesOf(UserSession userSession) throws IllegalArgumentException, HttpException, UnauthorizedException {
        try {
            HttpResponse<String> response = apiHttpConnector.get("/articles", userSession.getSessionId());
            if (response.statusCode() == 200)
                return articleJSONParser.parseJSONToArticleList(response.body());
            else{
                System.err.printf("Status code %d requesting all articles of %s.\nResponse body:\n%s\n",
                        response.statusCode(),
                        userSession.getSessionId(),
                        response.body());
                throw new IllegalArgumentException(response.body());
            }
        } catch (URISyntaxException | IOException | InterruptedException e) {
            System.err.printf("Exception getting all articles\nownerId = %s\n", userSession.getSessionId());
            throw new HttpException(String.format("[Error] Exception getting all articles\nownerId = %s\n%s\n",
                    userSession.getSessionId(), e.getMessage()));
        }
    }

    @Override
    public boolean suscribeToArticle(Article article, UserSession userSession) throws HttpException, IllegalArgumentException {
        try{
            String path = String.format("%s/articles/%s/users/", System.getenv("RESOURCE_URL"), article.getId());
            HttpResponse<String> response = apiHttpConnector.post(path, userSession.getSessionId());
            return response.statusCode() == 200;
        } catch (URISyntaxException | InterruptedException | IOException e) {
            throw new HttpException(String.format("[Error] Exception subscribing userId %s to articleId %s.\n%s\n",
                    userSession.getSessionId(), article.getId(), e.getMessage()));
        }
    }

    @Override
    public Article closeArticle(Article article, UserSession userSession) throws HttpException, IllegalArgumentException, UnauthorizedException {
        try{
            String path = String.format("%s/articles/%s/close", System.getenv("RESOURCE_URL"), article.getId());
            HttpResponse<String> response = apiHttpConnector.patch(path, "", userSession.getSessionId());
            if (response.statusCode() == 200){
                return articleJSONParser.parseJSONToArticle(response.body());
            } else{
                throw new IllegalArgumentException("Couldn't close article of id " + article.getId());
            }
        } catch (URISyntaxException | InterruptedException | IOException e) {
            throw new HttpException(String.format("[Error] Exception subscribing userId %s to articleId %s.\n%s\n",
                    userSession.getSessionId(), article.getId(), e.getMessage()));
        }
    }

    @Override
    public List<Annotation> viewArticleSubscriptions(Article article) throws HttpException {
        try{
            String path = String.format("%s/articles/%s/users", System.getenv("RESOURCE_URL"), article.getId());
            HttpResponse<String> response = apiHttpConnector.get(path);
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
