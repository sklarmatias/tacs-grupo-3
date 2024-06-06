package org.tacsbot.api.user.impl;

import lombok.Setter;
import org.tacsbot.api.user.UserApi;
import org.tacsbot.model.User;
import org.tacsbot.parser.user.UserParser;
import org.tacsbot.parser.user.impl.UserJSONParser;
import javax.naming.AuthenticationException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Setter
public class UserApiConnection implements UserApi {

    private UserParser userParser;

    public UserApiConnection(){
        userParser = new UserJSONParser();
    }

    public User logIn(String email, String password) throws AuthenticationException, IOException {

        User logInUser = new User(null, null, null, email, password);
        try{
            String json = userParser.parseUserToJSON(logInUser);
            System.out.println(json);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(System.getenv("RESOURCE_URL") + "/users/login"))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .header("Content-Type","application/json")
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            client.close();
            // all ok
            if (response.statusCode() == 200)
                return userParser.parseJSONToUser(response.body());
            // wrong login
            else if (response.statusCode() == 401)
                throw new AuthenticationException("Wrong credentials.");
            // api error
            else
                throw new IOException(String.format("[Error] Server error.\nStatus code = %d;\nBody = %s\nHeaders = %s\n",
                        response.statusCode(), response.body(), response.headers()));
        } catch (URISyntaxException | InterruptedException e) {
            System.out.printf("[Error]\n%s\n", e.getMessage());
            throw new IOException(e.getMessage());
        }
    }

        public void register(String name, String surname, String email, String password) throws IOException, IllegalArgumentException {
        User user = new User(null, name, surname, email, password);
        String json = userParser.parseUserToJSON(user);
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(System.getenv("RESOURCE_URL") + "/users/register"))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .header("Content-Type","application/json")
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            client.close();
            // all ok
            if (response.statusCode() == 201){
                return;
            } else if(response.statusCode() == 400 && response.body().equals(String.format("Error! Email %s already in use", email)))
                throw new IllegalArgumentException("Email unavailable");
            // error
            else
                throw new IOException(String.format("[Error] Server error.\nStatus code = %d;\nBody = %s\nHeaders = %s\n",
                        response.statusCode(), response.body(), response.headers()));
        } catch (URISyntaxException | InterruptedException e) {
            System.out.printf("[Error]\n%s\n", e.getMessage());
            throw new IOException(e.getMessage());
        }
    }

}
