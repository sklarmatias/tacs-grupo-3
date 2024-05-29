package ar.edu.utn.frba.tacs.service;

import ar.edu.utn.frba.tacs.helpers.hash.impl.GuavaHashingHelper;
import ar.edu.utn.frba.tacs.helpers.hash.HashingHelper;
import ar.edu.utn.frba.tacs.model.Annotation;
import ar.edu.utn.frba.tacs.model.Article;
import ar.edu.utn.frba.tacs.model.User;
import ar.edu.utn.frba.tacs.repository.user.UsersRepository;
import ar.edu.utn.frba.tacs.repository.user.impl.MongoUsersRepository;
import lombok.Setter;

import javax.security.auth.login.LoginException;
import java.util.List;

@Setter
public class UserService {

    private UsersRepository usersRepository;

    private HashingHelper hashingHelper;

    public UserService(){
        hashingHelper = new GuavaHashingHelper();
        usersRepository = new MongoUsersRepository();
    }

    public User getUser(String id) {
        return usersRepository.find(id);
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

    public String saveUser(User user) {
        user.setPass(hashingHelper.hash(user.getPass()));
        return usersRepository.save(user);
    }
    // TODO delete this method
    public void cleanUser(String id){
        usersRepository.delete(id);
    }

    public void updateUserAddArticle(String id, Article article){
        usersRepository.updateAddArticle(id,article);
    }
    public void updateAddAnnotation(String id, Annotation annotation){
        usersRepository.updateAddAnnotation(id, annotation);
    }

}
