package ar.edu.utn.frba.tacs.service;

import ar.edu.utn.frba.tacs.helpers.hash.impl.GuavaHashingHelper;
import ar.edu.utn.frba.tacs.model.User;
import ar.edu.utn.frba.tacs.repository.user.impl.InMemoryUsersRepository;
import com.mongodb.ServerAddress;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.transitions.Mongod;
import de.flapdoodle.embed.mongo.transitions.RunningMongodProcess;
import de.flapdoodle.reverse.TransitionWalker;
import org.junit.*;

import javax.security.auth.login.LoginException;
import static org.junit.Assert.assertEquals;

public class UserServiceTest {

    static UserService userService;
    @BeforeClass
    public static void setUp(){
        TransitionWalker.ReachedState<RunningMongodProcess> running = Mongod.instance().start(Version.Main.V7_0);
        ServerAddress serverAddress = new ServerAddress(String.valueOf(running.current().getServerAddress()));
        userService = new UserService("mongodb://" + serverAddress);
    }

    @Test
    public void saveHashedPassword(){

        String email = "thiago@tacs.com";
        String nonHashedPassword = "tacs2024";

        User originalUser = new User("thiago", "cabrera", email,nonHashedPassword);
        originalUser.setId(userService.saveUser(originalUser));

        User savedUser = userService.getUser(originalUser.getId());
        Assert.assertEquals(new GuavaHashingHelper().hash(nonHashedPassword), savedUser.getPass());
    }

    @Test
    public void loginWithHashedPassword() throws LoginException {

        String email = "thiago2@tacs.com";
        String nonHashedPassword = "tacs2024";

        User originalUser = new User("thiago", "cabrera", email,nonHashedPassword);
        originalUser.setId(userService.saveUser(originalUser));

        User savedUser = userService.getUser(originalUser.getId());
        User logedUser = userService.loginUser(email, nonHashedPassword);
        Assert.assertEquals(savedUser.getId(), logedUser.getId());
    }

    @Test
    public void saveUser() {
        User user = new User("juan","perez","jp@gmail.com","123456");
        user.setId(userService.saveUser(user));
        User userFromDB = userService.getUser(user.getId());
        assertEquals(user.getId(),userFromDB.getId());
    }

}
