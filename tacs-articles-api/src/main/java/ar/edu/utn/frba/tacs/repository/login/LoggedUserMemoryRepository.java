package ar.edu.utn.frba.tacs.repository.login;

import ar.edu.utn.frba.tacs.helpers.hash.impl.GuavaHashingHelper;
import ar.edu.utn.frba.tacs.model.Client;
import ar.edu.utn.frba.tacs.model.LoggedUser;
import ar.edu.utn.frba.tacs.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class LoggedUserMemoryRepository implements LoggedUserRepository {
    private static LoggedUserMemoryRepository instance;
    private HashMap<String,LoggedUser> loggedUserList = new HashMap<>();
    private GuavaHashingHelper hashingHelper;
    private int userCounter;
    private LoggedUserMemoryRepository() {
        loggedUserList = new HashMap<>();
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
    public String getLoggedUserId(String id) {
        return loggedUserList.get(id).getUserId();
    }

    @Override
    public LoggedUser logUser(User user, Client client) {
        String id = hashingHelper.hash(String.valueOf(userCounter));
        userCounter++;
        LoggedUser loggedUser = new LoggedUser(id,user.getId(),client,user.getName(),user.getSurname(),user.getEmail());
        loggedUserList.put(loggedUser.getSessionId(),loggedUser);
        return loggedUser;
    }

    @Override
    public void closeSession(String id) {
        loggedUserList.remove(id);
    }

    @Override
    public void closeAllSessions(String id) {
        listOpenSessions(getLoggedUserId(id)).forEach(a->closeSession(a.getSessionId()));
    }

    @Override
    public List<LoggedUser> listOpenSessions(String userId) {
        return loggedUserList.values().stream().filter(a-> Objects.equals(a.getUserId(), userId)).toList();
    }
}
