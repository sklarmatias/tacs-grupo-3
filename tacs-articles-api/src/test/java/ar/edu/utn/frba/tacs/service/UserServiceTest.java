package ar.edu.utn.frba.tacs.service;

import ar.edu.utn.frba.tacs.TestFunctions;
import ar.edu.utn.frba.tacs.controller.UserController;
import ar.edu.utn.frba.tacs.exception.DuplicatedEmailException;
import ar.edu.utn.frba.tacs.helpers.hash.impl.GuavaHashingHelper;
import ar.edu.utn.frba.tacs.model.Client;
import ar.edu.utn.frba.tacs.model.LoggedUser;
import ar.edu.utn.frba.tacs.model.User;
import ar.edu.utn.frba.tacs.repository.user.impl.InMemoryUsersRepository;
import com.mongodb.ServerAddress;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.transitions.Mongod;
import de.flapdoodle.embed.mongo.transitions.RunningMongodProcess;
import de.flapdoodle.reverse.TransitionWalker;
import org.junit.*;

import javax.security.auth.login.LoginException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class UserServiceTest {

    static UserService userService;
    static TransitionWalker.ReachedState<RunningMongodProcess> running;
    @BeforeClass
    public static void setUp(){
        running = Mongod.instance().start(Version.Main.V7_0);
        ServerAddress serverAddress = new ServerAddress(String.valueOf(running.current().getServerAddress()));
        userService = new UserService("mongodb://" + serverAddress);
    }
    @Before
    public void cleanDB(){
        List<User> usersList = userService.listUsers();
        for(User user : usersList){
            userService.delete(user.getId());
        }
    }
    @AfterClass
    public static void stop(){
        running.current().stop();

    }

    @Test
    public void saveHashedPassword() throws DuplicatedEmailException {

        String email = "thiago@tacs.com";
        String nonHashedPassword = "tacs2024";

        User originalUser = new User("thiago", "cabrera", email,nonHashedPassword);
        originalUser.setId(userService.saveUser(originalUser));

        User savedUser = userService.getUser(originalUser.getId());
        Assert.assertEquals(new GuavaHashingHelper().hash(nonHashedPassword), savedUser.getPass());
    }

    @Test
    public void loginWithHashedPassword() throws LoginException, DuplicatedEmailException {

        String email = "thiago2@tacs.com";
        String nonHashedPassword = "tacs2024";

        User originalUser = new User("thiago", "cabrera", email,nonHashedPassword);
        originalUser.setId(userService.saveUser(originalUser));

        User savedUser = userService.getUser(originalUser.getId());
        LoggedUser.LoggedUserDTO loggedUser = userService.loginUser(email, nonHashedPassword, Client.WEB);
        Assert.assertEquals(savedUser.getId(), userService.getLoggedUserId(loggedUser.getSessionId()));
    }

    @Test
    public void saveUser() throws DuplicatedEmailException {
        User user = new User("juan","perez","jp@gmail.com","123456");
        user.setId(userService.saveUser(user));
        User userFromDB = userService.getUser(user.getId());
        assertEquals(user.getId(),userFromDB.getId());
    }

    private String getDuplicatedEmailExceptionMessage(User user2){
        try{
            userService.saveUser(user2);
            return "";
        } catch (DuplicatedEmailException e){
            return e.getMessage();
        }
    }

    @Test
    public void saveDuplicatedUserThrowsException() throws DuplicatedEmailException {
        User user = new User("juan","perez","mailunico@gmail.com","123456");
        user.setId(userService.saveUser(user));
        User user2 = new User("juancito","pereza","mailunico@gmail.com","123456");
        assertThrows(DuplicatedEmailException.class, () -> userService.saveUser(user2));
        
        assertEquals("Error! Email mailunico@gmail.com already in use", getDuplicatedEmailExceptionMessage(user2));
    }

}
