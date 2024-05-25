package org.tacsbot.api.user;

import org.tacsbot.model.User;

import javax.naming.AuthenticationException;
import java.io.IOException;

public interface UserApi {

    User logIn(String email, String pass) throws AuthenticationException, IOException;

    User register(String name, String surname, String email, String password) throws IOException;

}
