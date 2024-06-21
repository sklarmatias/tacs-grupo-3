package org.tacsbot.api.report.impl;

import org.tacsbot.api.report.ReportApi;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ReportApiImpl implements ReportApi {

    private HttpRequest getReportRequest(String reportPath){
        try{
            return HttpRequest.newBuilder()
                    .uri(new URI(System.getenv("RESOURCE_URL") + reportPath))
                    .GET()
                    .header("Content-Type","application/json")
                    .build();
        } catch (URISyntaxException e){
            String errorMsg = String.format("Wrong URI trying to get report from %s", reportPath);
            System.err.println(errorMsg);
            throw new RuntimeException(errorMsg);
        }
    }

    private int getGenericReport(String resourcePath){
        try (HttpClient client = HttpClient.newHttpClient()){
            HttpResponse<String> response = client.send(getReportRequest(resourcePath), HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200){
                String errorMsg = String.format("Status code %d trying to get report from %s\nBody: %s", response.statusCode(), response.uri(), response.body());
                System.err.println(errorMsg);
                throw new RuntimeException(errorMsg);
            }
            return Integer.parseInt(response.body());
        } catch (IOException | InterruptedException e) {
            String errorMsg = String.format("[Error] Exception %s getting report:\n%s", e.getClass(), e.getMessage());
            System.err.println(errorMsg);
            e.printStackTrace();
            throw new RuntimeException(errorMsg);
        }
    }

    @Override
    public int getEngagedUsersCount() {
        return getGenericReport("/reports/engaged_users");
    }

    @Override
    public int getUsersCount() {
        return getGenericReport("/reports/users");

    }

    @Override
    public int getArticlesCount() {
        return getGenericReport("/reports/articles");
    }

    @Override
    public int getSuccessfulArticlesCount() {
        return getGenericReport("/reports/articles/success");
    }

    @Override
    public int getFailedArticlesCount() {
        return getGenericReport("/reports/articles/failed");
    }
}
