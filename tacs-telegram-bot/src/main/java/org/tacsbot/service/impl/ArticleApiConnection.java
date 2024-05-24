package org.tacsbot.service.impl;

import org.apache.http.HttpException;
import org.tacsbot.model.Article;
import org.tacsbot.service.ArticleApi;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class ArticleApiConnection implements ArticleApi {

    @Override
    public String createArticle(Article article) throws IllegalArgumentException, HttpException, IOException {
        String JSONArticle = ArticleJSONParser.parseArticleToJSON(article);
        System.out.println(JSONArticle);
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(System.getenv("RESOURCE_URL") + "/articles"))
                    .POST(HttpRequest.BodyPublishers.ofString(JSONArticle))
                    .header("Content-Type", "application/json")
                    .header("user", article.getOwner())
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            client.close();
            if (response.statusCode() == 201)
                return response.headers().firstValue("Location").get();
            else {
                System.out.printf("Response code %d creating the following Article:\n%s\nResponse body: %s",
                        response.statusCode(),
                        JSONArticle,
                        response.body());
                throw new IllegalArgumentException("Couldn't create article.");
            }
        } catch (URISyntaxException e) {
            System.out.println("URISyntaxException sending the following article:\n" + JSONArticle);
            throw new IllegalArgumentException(e);
        } catch (IOException e) {
            System.out.println("IOException sending the following article:\n" + JSONArticle);
            throw new IllegalArgumentException();
        } catch (InterruptedException e) {
            System.out.println("InterruptedException sending the following article:\n" + JSONArticle);
            throw new IllegalArgumentException();
        }
    }

    @Override
    public List<Article> getAllArticles() throws HttpException {
        return getArticlesOf(null);
    }

    @Override
    public List<Article> getArticlesOf(String ownerId) throws IllegalArgumentException, HttpException {
        try (HttpClient client = HttpClient.newHttpClient()){
            HttpRequest request;
            if (ownerId == null){
                request = HttpRequest.newBuilder()
                        .uri(new URI(System.getenv("RESOURCE_URL") + "/articles"))
                        .GET()
                        .build();
            } else{
                request = HttpRequest.newBuilder()
                        .uri(new URI(System.getenv("RESOURCE_URL") + "/articles"))
                        .header("user",ownerId)
                        .GET()
                        .build();
            }
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200)
                return ArticleJSONParser.parseJSONToArticleList(response.body());
            else{
                System.out.printf("Status code %d requesting all articles of %s.\nResponse body:\n%s\n",
                        response.statusCode(),
                        ownerId,
                        response.body());
                throw new IllegalArgumentException(response.body());
            }
        } catch (URISyntaxException | IOException | InterruptedException | IllegalArgumentException e) {
            System.out.printf("Exception getting all articles\nownerId = %s\n", ownerId);
            e.printStackTrace();
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void suscribeToArticle(Article article, String userId) {

    }

    @Override
    public void closeArticle(Article article) {

    }

    @Override
    public void viewArticleSubscriptions(Article article) {

    }

}
