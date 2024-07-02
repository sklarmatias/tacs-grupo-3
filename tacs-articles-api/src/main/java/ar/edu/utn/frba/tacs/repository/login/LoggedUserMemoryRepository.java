package ar.edu.utn.frba.tacs.repository.login;

import ar.edu.utn.frba.tacs.helpers.hash.impl.GuavaHashingHelper;
import ar.edu.utn.frba.tacs.model.Client;
import ar.edu.utn.frba.tacs.model.LoggedUser;
import ar.edu.utn.frba.tacs.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LoggedUserMemoryRepository implements LoggedUserRepository {
    private static LoggedUserMemoryRepository instance;
    private List<LoggedUser> loggedUserList = new ArrayList<>();
    private GuavaHashingHelper hashingHelper;
    private int userCounter;
    private LoggedUserMemoryRepository() {
        loggedUserList = new ArrayList<>();
        hashingHelper = new GuavaHashingHelper();
        userCounter = 0;
    }

    public static synchronized LoggedUserMemoryRepository getInstance() {
        if (instance == null) {
            instance = new LoggedUserMemoryRepository();
        }
        return instance;
    }

    @Override
    public String getLoggedUserId(String id, Client client) {
        return loggedUserList.stream().filter(a-> Objects.equals(a.getSessionId(), id) && a.getClient() == client).findFirst().get().getUserId();
    }

    @Override
    public LoggedUser logUser(User user, Client client) {
        String id = hashingHelper.hash(String.valueOf(userCounter));
        userCounter++;
        LoggedUser loggedUser = new LoggedUser(id,user.getId(),client,user.getName(),user.getSurname(),user.getEmail());
        loggedUserList.add(loggedUser);
        return loggedUser;
    }

    @Override
    public void closeSession(String id, Client client) {
        loggedUserList.removeAll(loggedUserList.stream().filter(a-> Objects.equals(a.getSessionId(), id) && a.getClient() == client).toList());
    }

    @Override
    public void closeAllSessions(String id) {
        String userId = loggedUserList.stream().filter(a-> Objects.equals(a.getSessionId(), id)).findFirst().get().getUserId();
        loggedUserList.removeAll(listOpenSessions(id));
    }

    @Override
    public List<LoggedUser> listOpenSessions(String userId) {
        return loggedUserList.stream().filter(a-> Objects.equals(a.getUserId(), userId)).toList();
    }
}
