package org.tacsbot.api.user.impl;

import org.tacsbot.model.User;
import org.tacsbot.parser.user.UserParser;
import org.tacsbot.parser.user.impl.UserJSONParser;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class UserHttpConnector {
    UserParser userParser = new UserJSONParser();
    public HttpResponse<String> loginUserConnector(User logInUser) throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(System.getenv("RESOURCE_URL") + "/users/login"))
                .POST(HttpRequest.BodyPublishers.ofString(userParser.parseUserToJSON(logInUser)))
                .header("Content-Type","application/json")
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();
        return response;
    }
    public HttpResponse<String> registerConnector(User user) throws IOException, InterruptedException, URISyntaxException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(System.getenv("RESOURCE_URL") + "/users/register"))
                .POST(HttpRequest.BodyPublishers.ofString(userParser.parseUserToJSON(user)))
                .header("Content-Type","application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();
        return response;
    }

}
