package ar.edu.utn.frba.tacs.service;

import ar.edu.utn.frba.tacs.helpers.GuavaHashingHelper;
import ar.edu.utn.frba.tacs.model.User;
import ar.edu.utn.frba.tacs.repository.user.impl.InMemoryUsersRepository;
import org.junit.Assert;
import org.junit.Test;
import javax.security.auth.login.LoginException;

public class UserServiceTest {

    @Test
    public void saveHashedPassword(){

        String email = "thiago1@tacs.com";
        String nonHashedPassword = "tacs2024";

        UserService userService = new UserService();
        userService.setUsersRepository(new InMemoryUsersRepository());

        User originalUser = new User("thiago", "cabrera", email,nonHashedPassword);
        userService.saveUser(originalUser);
        User savedUser = userService.getUser(originalUser.getId());
        Assert.assertEquals(new GuavaHashingHelper().hash(nonHashedPassword), savedUser.getPass());
    }

    @Test
    public void loginWithHashedPassword() throws LoginException {

        String email = "thiago2@tacs.com";
        String nonHashedPassword = "tacs2024";

        UserService userService = new UserService();
        userService.setUsersRepository(new InMemoryUsersRepository());

        User originalUser = new User("thiago", "cabrera", email,nonHashedPassword);
        userService.saveUser(originalUser);
        User savedUser = userService.getUser(originalUser.getId());
        User logedUser = userService.loginUser(email, nonHashedPassword);
        Assert.assertEquals(savedUser.getId(), logedUser.getId());
    }

}
