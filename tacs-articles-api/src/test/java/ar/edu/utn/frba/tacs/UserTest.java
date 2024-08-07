package ar.edu.utn.frba.tacs;

import ar.edu.utn.frba.tacs.controller.UserController;
import ar.edu.utn.frba.tacs.exception.DuplicatedEmailException;
import ar.edu.utn.frba.tacs.model.Annotation;
import ar.edu.utn.frba.tacs.model.Client;
import ar.edu.utn.frba.tacs.model.User;
import ar.edu.utn.frba.tacs.service.ArticleService;
import ar.edu.utn.frba.tacs.service.UserService;
import com.mongodb.ServerAddress;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.transitions.Mongod;
import de.flapdoodle.embed.mongo.transitions.RunningMongodProcess;
import de.flapdoodle.reverse.TransitionWalker;
import org.junit.*;

import javax.security.auth.login.LoginException;
import java.util.List;

public class UserTest {
    static UserService userService;
    static ArticleService articleService;
    static TestFunctions testFunctions;
    static UserController userController;
    static TransitionWalker.ReachedState<RunningMongodProcess> running;
    @BeforeClass
    public static void setUp(){
        running = Mongod.instance().start(Version.Main.V7_0);
        ServerAddress serverAddress = new ServerAddress(String.valueOf(running.current().getServerAddress()));
        userService = new UserService("mongodb://" + serverAddress);
        articleService = new ArticleService("mongodb://" + serverAddress);
        testFunctions = new TestFunctions(userService,articleService);
        userController = new UserController(userService);
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
    public void signUpUserRepeatedEmail() throws DuplicatedEmailException {
        User user = new User("juan","perez","abcd@gmail.com","123456");
        user.setId(userService.saveUser(user));
        User secondUser = new User("jose","perez","abcd@gmail.com","12345678");
        Assert.assertThrows(IllegalArgumentException.class, () -> userService.saveUser(secondUser));

    }


    @Test
    public void userWithAnnotationHasInteracted(){
        User user =testFunctions.createTestUser();
        user.addAnnotation(new Annotation());

        Assert.assertTrue(user.hasInteracted());

    }

    @Test
    public void userWithArticleHasInteracted(){
        User user = testFunctions.createTestUser();
        user.getPostedArticles().add(testFunctions.createTestArticle(user.getId()));

        Assert.assertTrue(user.hasInteracted());

    }

    @Test
    public void userHasntInteracted(){
        User user = testFunctions.createTestUser();
        Assert.assertFalse(user.hasInteracted());
    }
    @Test
    public void testLoginWrong(){
        Assert.assertThrows(LoginException.class,()->userService.loginUser("wrong@gmail.com","1234", Client.WEB));
    }
    @Test
    public void testUpdateUser(){
        User user = testFunctions.createTestUser();
        user.setName("pablo");
        user.setSurname("alvarez");
        userService.updateUser(user.getId(),user);
        User usernew = userService.listUsers().get(0);
        Assert.assertEquals(usernew.getId(),user.getId());
        Assert.assertEquals("pablo",usernew.getName());
        Assert.assertEquals("alvarez",usernew.getSurname());
    }

}
