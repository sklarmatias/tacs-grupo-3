package ar.edu.utn.frba.tacs.service;

import ar.edu.utn.frba.tacs.model.Annotation;
import ar.edu.utn.frba.tacs.model.Article;
import ar.edu.utn.frba.tacs.model.User;
import ar.edu.utn.frba.tacs.repository.objectMappers.MongoAnnotationMapper;
import ar.edu.utn.frba.tacs.repository.objectMappers.MongoArticleMapper;
import ar.edu.utn.frba.tacs.repository.user.UsersRepository;
import ar.edu.utn.frba.tacs.repository.user.impl.InMemoryUsersRepository;
import ar.edu.utn.frba.tacs.repository.user.impl.MongoUsersRepository;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;

import java.util.List;

public class UserService {

    private final UsersRepository usersRepository = new MongoUsersRepository();

    public User getUser(String id) {
        return usersRepository.find(id);
    }

    public User loginUser(String email, String pass) { return usersRepository.find(email, pass); }

    public List<User> listUsers() {
        return usersRepository.findAll();
    }

    public void updateUser(String id, User user) {
        usersRepository.update(id, user);
    }

    public String saveUser(User user) {
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
