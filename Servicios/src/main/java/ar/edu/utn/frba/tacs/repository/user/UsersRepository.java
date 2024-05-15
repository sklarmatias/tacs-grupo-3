package ar.edu.utn.frba.tacs.repository.user;

import ar.edu.utn.frba.tacs.model.User;

import java.util.List;

public interface UsersRepository {

    List<User> findAll();

    User find(Integer id);
    User find(String email, String pass);
    void update(Integer id, User user);

    Integer save(User user);

    void delete();
}
