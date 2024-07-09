package org.tacsbot.api.user.impl;

import lombok.Setter;
import org.tacsbot.api.user.UserApi;
import org.tacsbot.api.utils.ApiHttpConnector;
import org.tacsbot.model.User;
import org.tacsbot.model.UserSession;
import org.tacsbot.parser.user.UserParser;
import org.tacsbot.parser.user.impl.UserJSONParser;
import javax.naming.AuthenticationException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;

@Setter
public class UserApiConnection implements UserApi {

    private UserParser userParser;
    @Setter
    private ApiHttpConnector apiHttpConnector = new ApiHttpConnector();

    public UserApiConnection(){
        userParser = new UserJSONParser();
    }

    public UserSession logIn(String email, String password) throws AuthenticationException, IOException {

        User user = new User(email, password);
        try {
            HttpResponse<String> response = apiHttpConnector.post("/users/login", userParser.parseUserToJSON(user));
            if (response.statusCode() == 200)
                return userParser.parseJSONToUserSession(response.body());
                // wrong login
            else if (response.statusCode() == 401)
                throw new AuthenticationException("Wrong credentials.");
                // api error
            else
                throw new IOException(String.format("[Error] Server error.\nStatus code = %d;\nBody = %s\nHeaders = %s\n",
                        response.statusCode(), response.body(), response.headers()));
        } catch (URISyntaxException | InterruptedException e) {
            System.err.printf("[Error] Connection error\n%s\n", e.getMessage());
            throw new IOException(e.getMessage());
        }
    }

        public void register(String name, String surname, String email, String password) throws IOException, IllegalArgumentException {
            User user = new User(null, name, surname, email, password);
            try {
                HttpResponse<String> response = apiHttpConnector.post("/users/register", userParser.parseUserToJSON(user));
                if(response.statusCode() == 400 && response.body().equals(String.format("Error! Email %s already in use", email)))
                    throw new IllegalArgumentException("Email unavailable");
                    // error
                else if (response.statusCode() != 201)
                    throw new IOException(String.format("[Error] Server error.\nStatus code = %d;\nBody = %s\nHeaders = %s\n",
                            response.statusCode(), response.body(), response.headers()));
            } catch (URISyntaxException | InterruptedException e) {
                System.err.printf("[Error]\n%s\n", e.getMessage());
                throw new IOException(e.getMessage());
            }
    }

}
