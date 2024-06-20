package ar.edu.utn.frba.tacs;

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
import org.bson.Document;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserTest {
    static UserService userService;
    static ArticleService articleService;
    static TestFunctions testFunctions;
    @BeforeClass
    public static void setUp(){
        TransitionWalker.ReachedState<RunningMongodProcess> running = Mongod.instance().start(Version.Main.V7_0);
        ServerAddress serverAddress = new ServerAddress(String.valueOf(running.current().getServerAddress()));
        userService = new UserService("mongodb://" + serverAddress);
        articleService = new ArticleService("mongodb://" + serverAddress);
        testFunctions = new TestFunctions(userService,articleService);
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

}
