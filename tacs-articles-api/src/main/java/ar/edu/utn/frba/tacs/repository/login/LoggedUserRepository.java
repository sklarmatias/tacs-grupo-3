package ar.edu.utn.frba.tacs.repository.login;

import ar.edu.utn.frba.tacs.model.Client;
import ar.edu.utn.frba.tacs.model.LoggedUser;
import ar.edu.utn.frba.tacs.model.User;

import java.util.List;

public interface LoggedUserRepository {
    public String getLoggedUserId(String id);
    public LoggedUser logUser(User user, Client client);
    public void closeSession(String id);
    public void closeAllSessions(String id);
    public List<LoggedUser> listOpenSessions(String userId);
}
