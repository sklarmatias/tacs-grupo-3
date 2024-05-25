package org.tacsbot.service.parser.user;

import org.tacsbot.model.User;
import java.io.IOException;

public interface UserParser {

    String parsUserToJSON(User user) throws IOException;

    User parseJSONToUser(String json);

}
