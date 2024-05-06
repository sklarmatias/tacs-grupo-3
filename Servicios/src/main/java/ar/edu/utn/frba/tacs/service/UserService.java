package ar.edu.utn.frba.tacs.service;

import ar.edu.utn.frba.tacs.model.User;
import ar.edu.utn.frba.tacs.repository.user.UsersRepository;
import ar.edu.utn.frba.tacs.repository.user.impl.InMemoryUsersRepository;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;

import java.util.List;

public class UserService {

    private final UsersRepository usersRepository = new InMemoryUsersRepository();

    public User getUser(Integer id) {
        return usersRepository.find(id);
    }

    public User loginUser(String email, String pass) { return usersRepository.find(email, pass); }

    public List<User> listUsers() {
        return usersRepository.findAll();
    }

    public void updateUser(int id, User user) {
        usersRepository.update(id, user);
    }

    public Integer saveUser(String name, String surname, String email, String pass) {
        return usersRepository.save(new User(name, surname, email,pass));
    }

    // TODO delete this method
    public void cleanUsers(){
        usersRepository.delete();
    }

}
