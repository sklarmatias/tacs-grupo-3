package ar.edu.utn.frba.tacs;

import static org.junit.Assert.assertEquals;

import ar.edu.utn.frba.tacs.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.cxf.jaxrs.client.WebClient;
import org.junit.Assert;
import org.junit.Test;
import ar.edu.utn.frba.tacs.model.User;
import jakarta.ws.rs.core.Response;

public class UserTest {
    UserService userService = new UserService();
    @Test
    public void testAddUsuario() {

        User user = createTestUser();
        User userFromDB = userService.getUser(user.getId());
        assertEquals(user.getId(),userFromDB.getId());
        userService.cleanUser(user.getId());
    }
    @Test
    public void testLogin(){
        User user = createTestUser();
        User userLogin = userService.loginUser(user.getEmail(), user.getPass());
        assertEquals(user.getId(),userLogin.getId());
        userService.cleanUser(user.getId());
    }
    private User createTestUser(){
        User user = new User("juan","perez","jp@gmail.com","123456");
        user.setId(userService.saveUser(user));
        return user;
    }
}
