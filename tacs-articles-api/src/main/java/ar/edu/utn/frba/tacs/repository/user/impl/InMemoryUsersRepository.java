package ar.edu.utn.frba.tacs.repository.user.impl;

import ar.edu.utn.frba.tacs.repository.user.UsersRepository;
import ar.edu.utn.frba.tacs.model.User;

import java.util.ArrayList;
import java.util.List;
public class InMemoryUsersRepository implements UsersRepository {

    private static final List<User> USERS = new ArrayList<>();
    private static Integer key = 0;


    @Override
    public List<User> findAll() {
        return new ArrayList<>(USERS);
    }

    @Override
    public User find(String id) {
        return USERS.stream().filter(us -> us.getId().equals(id)).findFirst().get();
    }

    @Override
    public User find(String email, String pass){ return USERS.stream().filter(us->us.getEmail().equals(email) && us.getPass().equals(pass)).findFirst().get();}
    @Override
    public void update(String id, User user) {
        User useroriginal = USERS.stream().filter(us -> us.getId().equals(id)).findFirst().get();
        if (user.getName() != null) {
            useroriginal.setName(user.getName());
        }
        if (user.getSurname() != null) {
            useroriginal.setSurname(user.getSurname());
        }
        if (user.getEmail() != null) {
            useroriginal.setEmail(user.getEmail());
        }
    }

    @Override
    public String save(User user) {
        if (user.getName() == null)
            throw new IllegalArgumentException("\"nombre\" field required");
        if (user.getSurname() == null)
            throw new IllegalArgumentException("\"apellido\" field required");
        if (user.getEmail() == null)
            throw new IllegalArgumentException("\"email\" field required");
        key += 1;
        user.setId(key.toString());
        USERS.add(user);
        return key.toString();
    }

    @Override
    public void delete(){
        USERS.clear();
    }
}
