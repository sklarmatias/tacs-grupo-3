package ar.edu.utn.frba.tacs.repository.login;

import ar.edu.utn.frba.tacs.model.Client;
import ar.edu.utn.frba.tacs.model.LoggedUser;

import java.util.List;

public interface LoggedUserRepository {
    public String getLoggedUserId(String id, Client client);
    public String logUser(String userId, Client client);
    public void closeSession(String id, Client client);
    public void closeAllSessions(String id);
    public List<LoggedUser> listOpenSessions(String userId);
}
