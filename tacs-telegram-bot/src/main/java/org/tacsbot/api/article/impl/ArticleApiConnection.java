package org.tacsbot.api.article.impl;

import lombok.Setter;
import org.apache.http.HttpException;
import org.tacsbot.api.article.ArticleApi;
import org.tacsbot.model.Article;
import org.tacsbot.parser.article.impl.ArticleJSONParser;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.util.List;

@Setter
public class ArticleApiConnection implements ArticleApi {

    private ArticleHttpConnector articleHttpConnector;

    public ArticleApiConnection(){
        articleHttpConnector = new ArticleHttpConnector();
    }

    @Override
    public String createArticle(Article article) throws IllegalArgumentException, HttpException, IOException {
        String JSONArticle = new ArticleJSONParser().parseArticleToJSON(article);
        System.out.println(JSONArticle);
        try {
            HttpResponse<String> response = articleHttpConnector.createArticleConnector(JSONArticle, article.getOwner());
            if (response.statusCode() == 201)
                return response.headers().firstValue("Location").get();
            else {
                System.out.printf("Response code %d creating the following Article:\n%s\nResponse body: %s",
                        response.statusCode(),
                        JSONArticle,
                        response.body());
                throw new IllegalArgumentException("Couldn't create article.");
            }
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new HttpException(String.format("[Error] Sending the following article:\n%s\n%s\n",
                    JSONArticle, e.getMessage()));
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
                return new ArticleJSONParser().parseJSONToArticleList(response.body());
            else{
                System.out.printf("Status code %d requesting all articles of %s.\nResponse body:\n%s\n",
                        response.statusCode(),
                        ownerId,
                        response.body());
                throw new IllegalArgumentException(response.body());
            }
        } catch (URISyntaxException | IOException | InterruptedException e) {
            System.out.printf("Exception getting all articles\nownerId = %s\n", ownerId);
            throw new HttpException(String.format("Exception getting all articles\nownerId = %s\n%s\n",
                    ownerId, e.getMessage()));
        }
    }

    @Override
    public boolean suscribeToArticle(Article article, String userId) throws HttpException, IllegalArgumentException {
        try{
            HttpResponse<String> response = articleHttpConnector.suscribeToArticle(article.getId(), userId);
            return response.statusCode() == 200;
        } catch (URISyntaxException | InterruptedException | IOException e) {
            throw new HttpException(String.format("Exception subscribing userId %s to articleId %s.\n%s\n",
                    userId, article.getId(), e.getMessage()));
        }
    }

    @Override
    public void closeArticle(Article article) {

    }

    @Override
    public void viewArticleSubscriptions(Article article) {

    }

}
