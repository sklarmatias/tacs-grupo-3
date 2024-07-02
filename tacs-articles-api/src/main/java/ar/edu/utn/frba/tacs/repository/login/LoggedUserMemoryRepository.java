package ar.edu.utn.frba.tacs.repository.login;

import ar.edu.utn.frba.tacs.helpers.hash.impl.GuavaHashingHelper;
import ar.edu.utn.frba.tacs.model.Client;
import ar.edu.utn.frba.tacs.model.LoggedUser;

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
        return loggedUserList.stream().filter(a-> Objects.equals(a.getId(), id) && a.getClient() == client).findFirst().get().getUserId();
    }

    @Override
    public String logUser(String userId, Client client) {
        String id = hashingHelper.hash(String.valueOf(userCounter));
        userCounter++;
        loggedUserList.add(new LoggedUser(id,userId,client));
        return id;
    }

    @Override
    public void closeSession(String id, Client client) {
        loggedUserList.removeAll(loggedUserList.stream().filter(a-> Objects.equals(a.getId(), id) && a.getClient() == client).toList());
    }

    @Override
    public void closeAllSessions(String id) {
        String userId = loggedUserList.stream().filter(a-> Objects.equals(a.getId(), id)).findFirst().get().getUserId();
        loggedUserList.removeAll(listOpenSessions(id));
    }

    @Override
    public List<LoggedUser> listOpenSessions(String userId) {
        return loggedUserList.stream().filter(a-> Objects.equals(a.getUserId(), userId)).toList();
    }
}
