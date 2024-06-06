package org.tacsbot.api.notification.impl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class NotificationHttpConnector {

    private final HttpClient client = HttpClient.newHttpClient();

    public HttpResponse<String> getPendingNotificationsConnector() throws IOException, InterruptedException, URISyntaxException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(System.getenv("RESOURCE_URL") + "/pendingNotifications"))
                .GET()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> markAsNotifiedConnector(String notificationId) throws IOException, InterruptedException, URISyntaxException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(System.getenv("RESOURCE_URL") + "/pendingNotifications/markAsNotified/" + notificationId))
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
