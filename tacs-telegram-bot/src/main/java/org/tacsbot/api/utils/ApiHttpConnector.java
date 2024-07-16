package org.tacsbot.api.utils;

import org.tacsbot.exceptions.UnauthorizedException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiHttpConnector {

    private HttpRequest.Builder createBasicRequestBuilder(String path) throws URISyntaxException {
        return HttpRequest.newBuilder()
                .uri(new URI(System.getenv("RESOURCE_URL") + path))
                .header("Content-Type", "application/json")
                .header("client", "BOT");
    }

    private HttpRequest.Builder createBasicRequestBuilder(String path, String sessionId) throws URISyntaxException {
        return HttpRequest.newBuilder()
                .uri(new URI(System.getenv("RESOURCE_URL") + path))
                .header("Content-Type", "application/json")
                .header("client", "BOT")
                .header("session", sessionId);
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();
        return response;
    }

    // sessionless variant
    public HttpResponse<String> post(String path, String body) throws IOException, InterruptedException, URISyntaxException {

        HttpRequest request = createBasicRequestBuilder(path)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        return sendRequest(request);
    }

    public HttpResponse<String> post(String path, String body, String sessionId) throws IOException, InterruptedException, URISyntaxException, UnauthorizedException {
        HttpRequest request = createBasicRequestBuilder(path, sessionId)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> response = sendRequest(request);
        if (response.statusCode() == 401){
            throw new UnauthorizedException(sessionId);
        }
        return response;
    }

    // sessionless variant
    public HttpResponse<String> get(String path, String sessionId) throws IOException, InterruptedException, URISyntaxException, UnauthorizedException {
        HttpRequest request = createBasicRequestBuilder(path, sessionId)
                .GET()
                .build();
        HttpResponse<String> response = sendRequest(request);
        if (response.statusCode() == 401){
            throw new UnauthorizedException(sessionId);
        }
        return response;
    }

    public HttpResponse<String> get(String path) throws IOException, InterruptedException, URISyntaxException {
        HttpRequest request = createBasicRequestBuilder(path)
                .GET()
                .build();
        return sendRequest(request);
    }

    public HttpResponse<String> patch(String path, String body, String sessionId) throws IOException, InterruptedException, URISyntaxException, UnauthorizedException {
        HttpRequest request = createBasicRequestBuilder(path, sessionId)
                .method("PATCH", HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> response = sendRequest(request);
        if (response.statusCode() == 401){
            throw new UnauthorizedException(sessionId);
        }
        return response;
    }

}
