package org.tacsbot.api.user;

import org.tacsbot.model.User;
import org.tacsbot.model.UserSession;

import javax.naming.AuthenticationException;
import java.io.IOException;
import java.net.URISyntaxException;

public interface UserApi {

    UserSession logIn(String email, String pass) throws AuthenticationException, IOException, URISyntaxException, InterruptedException;

    void register(String name, String surname, String email, String password) throws IOException;

}
