package org.tacsbot.api.report.impl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ReportHttpConnector {
    public HttpResponse<String> getReportsConnector(String reportPath) throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(System.getenv("RESOURCE_URL") + reportPath))
                    .GET()
                    .header("Content-Type","application/json")
                    .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();
        return response;
    }
}
