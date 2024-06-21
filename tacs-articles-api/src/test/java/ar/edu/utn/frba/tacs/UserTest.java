package ar.edu.utn.frba.tacs;

import ar.edu.utn.frba.tacs.controller.UserController;
import ar.edu.utn.frba.tacs.exception.DuplicatedEmailException;
import ar.edu.utn.frba.tacs.model.Annotation;
import ar.edu.utn.frba.tacs.model.Article;
import ar.edu.utn.frba.tacs.model.User;
import ar.edu.utn.frba.tacs.service.ArticleService;
import ar.edu.utn.frba.tacs.service.UserService;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.transitions.Mongod;
import de.flapdoodle.embed.mongo.transitions.RunningMongodProcess;
import de.flapdoodle.reverse.TransitionWalker;
import jakarta.ws.rs.core.Response;
import org.bson.Document;
import org.junit.*;
import org.junit.jupiter.api.BeforeEach;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.Date;
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
        User user = testFunctions.createTestUser();
        Assert.assertThrows(LoginException.class,()->userService.loginUser("wrong@gmail.com","1234"));
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
    @Test
    public void testControllerListUsers(){
        Assert.assertEquals(0,userController.listUsers().size());
        User user1 = testFunctions.createTestUser();
        User user2 = testFunctions.createTestUser();
        Assert.assertEquals(2,userController.listUsers().size());
    }
    @Test
    public void testControllerGetUser(){
        User user1 = testFunctions.createTestUser();
        User.UserDTO userDTO = userController.getUser(user1.getId());
        Assert.assertEquals(user1.getName(),userDTO.getName());
        Assert.assertEquals(user1.getId(),userDTO.getId());
    }
    @Test
    public void testControllerUpdateUser(){
        User user1 = testFunctions.createTestUser();
        Assert.assertEquals("juan",user1.getName());
        user1.setName("pedro");
        userController.updateUser(user1.getId(),user1);
        Assert.assertEquals("pedro",user1.getName());
    }
    @Test
    public void testControllerSaveUser(){
        User user1 = new User("pedro","perez","a@b.com","123456");
        Assert.assertEquals(0,userController.listUsers().size());
        Response response = userController.saveUser(user1);
        Assert.assertEquals(201,response.getStatus());
        Assert.assertEquals(1,userController.listUsers().size());
    }
    @Test
    public void testControllerLogin() throws LoginException {
        User user = testFunctions.createTestUser();
        User userLogin = new User();
        userLogin.setEmail(user.getEmail());
        userLogin.setPass("123456");
        User.UserDTO userDTO = userController.loginUser(userLogin);
        Assert.assertEquals(user.getId(),userDTO.getId());
        Assert.assertEquals(user.getName(),userDTO.getName());
    }
    @Test
    public void testControllerSaveUserDuplicatedEmail(){
        User user1 = new User("pedro","perez","a@b.com","123456");
        Response response = userController.saveUser(user1);
        Assert.assertEquals(201,response.getStatus());
        response = userController.saveUser(user1);
        Assert.assertEquals(400,response.getStatus());

    }
}
