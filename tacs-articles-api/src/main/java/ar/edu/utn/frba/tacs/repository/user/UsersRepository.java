package ar.edu.utn.frba.tacs.repository.user;

import ar.edu.utn.frba.tacs.model.Annotation;
import ar.edu.utn.frba.tacs.model.Article;
import ar.edu.utn.frba.tacs.model.User;

import java.util.List;

public interface UsersRepository {

    List<User> findAll();

    User find(String id);
    User find(String email, String pass);
    void update(String id, User user);

    String save(User user);

    void delete(String id);
    void updateAddArticle(String id, Article article);
    void updateAddAnnotation(String id, Annotation annotation);
}
