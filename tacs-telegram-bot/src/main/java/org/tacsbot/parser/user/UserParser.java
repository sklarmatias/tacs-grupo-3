package org.tacsbot.parser.user;

import org.tacsbot.model.User;
import java.io.IOException;

public interface UserParser {

    String parseUserToJSON(User user) throws IOException;

    User parseJSONToUser(String json);

}
