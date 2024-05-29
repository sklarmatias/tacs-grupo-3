package ar.edu.utn.frba.tacs.service;

import ar.edu.utn.frba.tacs.helpers.hash.impl.GuavaHashingHelper;
import ar.edu.utn.frba.tacs.model.User;
import ar.edu.utn.frba.tacs.repository.user.impl.InMemoryUsersRepository;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import javax.security.auth.login.LoginException;
import static org.junit.Assert.assertEquals;

public class UserServiceTest {

    UserService userService;

    @Before
    public void createEntities(){
        userService = new UserService();
        userService.setUsersRepository(new InMemoryUsersRepository());
    }

    @Test
    public void saveHashedPassword(){

        String email = "thiago@tacs.com";
        String nonHashedPassword = "tacs2024";

        User originalUser = new User("thiago", "cabrera", email,nonHashedPassword);
        userService.saveUser(originalUser);

        User savedUser = userService.getUser(originalUser.getId());
        Assert.assertEquals(new GuavaHashingHelper().hash(nonHashedPassword), savedUser.getPass());
    }

    @Test
    public void loginWithHashedPassword() throws LoginException {

        String email = "thiago2@tacs.com";
        String nonHashedPassword = "tacs2024";

        User originalUser = new User("thiago", "cabrera", email,nonHashedPassword);
        userService.saveUser(originalUser);

        User savedUser = userService.getUser(originalUser.getId());
        User logedUser = userService.loginUser(email, nonHashedPassword);
        Assert.assertEquals(savedUser.getId(), logedUser.getId());
    }

    @Test
    public void saveUser() {
        User user = new User("juan","perez","jp@gmail.com","123456");
        userService.saveUser(user);
        User userFromDB = userService.getUser(user.getId());
        assertEquals(user.getId(),userFromDB.getId());
    }

}
