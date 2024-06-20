package ar.edu.utn.frba.tacs.service;

import ar.edu.utn.frba.tacs.exception.DuplicatedEmailException;
import ar.edu.utn.frba.tacs.helpers.hash.impl.GuavaHashingHelper;
import ar.edu.utn.frba.tacs.helpers.hash.HashingHelper;
import ar.edu.utn.frba.tacs.model.Annotation;
import ar.edu.utn.frba.tacs.model.Article;
import ar.edu.utn.frba.tacs.model.User;
import ar.edu.utn.frba.tacs.repository.articles.ArticlesRepository;
import ar.edu.utn.frba.tacs.repository.articles.impl.MongoArticlesRepository;
import ar.edu.utn.frba.tacs.repository.user.UsersRepository;
import ar.edu.utn.frba.tacs.repository.user.impl.MongoUsersRepository;
import jakarta.ws.rs.core.Response;
import lombok.Setter;

import javax.security.auth.login.LoginException;
import java.util.List;

@Setter
public class UserService {

    private final UsersRepository usersRepository;
    private HashingHelper hashingHelper = new GuavaHashingHelper();

    public UserService(){
        usersRepository = new MongoUsersRepository();
    }
    public UserService(String url){
        usersRepository = new MongoUsersRepository(url);
    }

    public User getUser(String id) {
        return usersRepository.find(id);
    }
    public boolean userExists(String email){
        return usersRepository.userExists(email);
    }

    public User loginUser(String email, String pass) throws LoginException {
        try{
            String hashedPass = hashingHelper.hash(pass);
            return usersRepository.find(email, hashedPass);
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

}
