package ar.edu.utn.frba.tacs.service;

import ar.edu.utn.frba.tacs.exception.DuplicatedEmailException;
import ar.edu.utn.frba.tacs.helpers.hash.impl.GuavaHashingHelper;
import ar.edu.utn.frba.tacs.helpers.hash.HashingHelper;
import ar.edu.utn.frba.tacs.model.*;
import ar.edu.utn.frba.tacs.repository.login.LoggedUserMemoryRepository;
import ar.edu.utn.frba.tacs.repository.login.LoggedUserRepository;
import ar.edu.utn.frba.tacs.repository.user.UsersRepository;
import ar.edu.utn.frba.tacs.repository.user.impl.MongoUsersRepository;

import javax.security.auth.login.LoginException;
import java.util.List;

public class UserService {

    private final UsersRepository usersRepository;
    private final HashingHelper hashingHelper = new GuavaHashingHelper();
    private final LoggedUserRepository loggedUserRepository = LoggedUserMemoryRepository.getInstance();

    public UserService(String url){
        usersRepository = new MongoUsersRepository(url);
    }

    public User getUser(String id) {
        return usersRepository.find(id);
    }
    public boolean userExists(String email){
        return usersRepository.userExists(email);
    }

    public LoggedUser.LoggedUserDTO loginUser(String email, String pass, Client client) throws LoginException {
        try{
            System.out.println("Intentando obtener session en memoria. Email: " + email + ", pass: " + pass + ", cliente: " + client);
            String hashedPass = hashingHelper.hash(pass);
            User user = usersRepository.find(email, hashedPass);
            return loggedUserRepository.logUser(user, client).convertToDTO();
        } catch(IndexOutOfBoundsException e){
            throw new LoginException("Wrong username or password.");
        }
    }

    public List<User> listUsers() {
        return usersRepository.findAll();
    }

    public void updateUser(String id, User user) {
        usersRepository.update(id, user);
    }

    public String saveUser(User user) throws DuplicatedEmailException {
        if(userExists(user.getEmail())){
            throw new DuplicatedEmailException(user.getEmail());
        }
        user.setPass(hashingHelper.hash(user.getPass()));
        return usersRepository.save(user);
    }

    public void updateUserAddArticle(String id, Article article){
        usersRepository.updateAddArticle(id,article);
    }
    public void updateAddAnnotation(String id, Annotation annotation){
        usersRepository.updateAddAnnotation(id, annotation);
    }
    public void delete(String id){
        usersRepository.delete(id);
    }
    public List<LoggedUser.LoggedUserDTO> listUserSessions(String id){
        return loggedUserRepository.listOpenSessions(loggedUserRepository.getLoggedUserId(id)).stream().map(LoggedUser::convertToDTO).toList();
    }
    public void closeUserSession(String id){
        loggedUserRepository.closeSession(id);
    }
    public void closeAllUserSessions(String id){
        loggedUserRepository.closeAllSessions(id);
    }
    public String getLoggedUserId(String id){
        return loggedUserRepository.getLoggedUserId(id);
    }
}
