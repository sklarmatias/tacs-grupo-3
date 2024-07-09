package org.tacsbot.parser.user;

import org.tacsbot.model.User;
import org.tacsbot.model.UserSession;

import java.io.IOException;

public interface UserParser {

    String parseUserToJSON(User user) throws IOException;

    User parseJSONToUser(String json);

    UserSession parseJSONToUserSession(String json);

    String parseUserSessionToJSON(UserSession userSession) throws IOException;

}
