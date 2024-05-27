package org.tacsbot.api.article.impl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ArticleHttpConnector {

    public HttpResponse<String> createArticleConnector(String json, String ownerId) throws IOException, InterruptedException, URISyntaxException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(System.getenv("RESOURCE_URL") + "/articles"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .header("user", ownerId)
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();
        return response;
    }

    public HttpResponse<String> getArticles(String ownerId) throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request;
        if (ownerId == null){
            request = HttpRequest.newBuilder()
                    .uri(new URI(System.getenv("http://localhost:8080/tacsWSREST") + "/articles"))
                    .GET()
                    .build();
        } else{
            request = HttpRequest.newBuilder()
                    .uri(new URI(System.getenv("http://localhost:8080/tacsWSREST") + "/articles"))
                    .header("user",ownerId)
                    .GET()
                    .build();
        }
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();
        return response;
    }

    public HttpResponse<String> suscribeToArticle(String articleId, String userId) throws URISyntaxException, IOException, InterruptedException {
        String url = String.format("%s/articles/%s/users/",
                System.getenv("RESOURCE_URL"), articleId);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .header("user", userId)
                .header("Content-Type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();
        return response;
    }

    public HttpResponse<String> closeArticle(String articleId, String userId) throws URISyntaxException, IOException, InterruptedException{
        String url = String.format("%s/articles/%s/close",
                System.getenv("RESOURCE_URL"), articleId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .header("user",userId)
                .header("Content-Type","application/json")
                .method("PATCH",HttpRequest.BodyPublishers.ofString(""))
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request,HttpResponse.BodyHandlers.ofString());
        client.close();
        return response;
    }

    public HttpResponse<String> getSubscriptions(String articleId) throws IOException, InterruptedException, URISyntaxException {
        String url = String.format("%s/articles/%s/users",
                System.getenv("RESOURCE_URL"), articleId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .GET()
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request,HttpResponse.BodyHandlers.ofString());
        client.close();
        return response;
    }

}
