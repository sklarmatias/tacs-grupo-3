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
    @Setter
    private UserHttpConnector userHttpConnector = new UserHttpConnector();

    public UserApiConnection(){
        userParser = new UserJSONParser();
    }

    public User logIn(String email, String password) throws AuthenticationException, IOException {

        User logInUser = new User(null, null, null, email, password);
        try{
            HttpResponse<String> response = userHttpConnector.loginUserConnector(logInUser);
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
            System.err.printf("[Error]\n%s\n", e.getMessage());
            throw new IOException(e.getMessage());
        }
    }

        public void register(String name, String surname, String email, String password) throws IOException, IllegalArgumentException {
        User user = new User(null, name, surname, email, password);
        try {
            HttpResponse<String> response = userHttpConnector.registerConnector(user);
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
            System.err.printf("[Error]\n%s\n", e.getMessage());
            throw new IOException(e.getMessage());
        }
    }

}
