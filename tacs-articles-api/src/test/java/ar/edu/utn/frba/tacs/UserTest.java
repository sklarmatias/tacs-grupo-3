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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserTest {
    static UserService userService;
    @BeforeClass
    public static void setUp(){
        TransitionWalker.ReachedState<RunningMongodProcess> running = Mongod.instance().start(Version.Main.V7_0);
        ServerAddress serverAddress = new ServerAddress(String.valueOf(running.current().getServerAddress()));
        userService = new UserService("mongodb://" + serverAddress);
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
        User user = new User("thiago", "cabrera", "thiago@tacs.com","tacs2024");
        user.addAnnotation(new Annotation());

        Assert.assertTrue(user.hasInteracted());

    }

    @Test
    public void userWithArticleHasInteracted(){
        User user = new User("thiago", "cabrera", "thiago@tacs.com","tacs2024");
        user.getPostedArticles().add(new Article());

        Assert.assertTrue(user.hasInteracted());

    }

    @Test
    public void userHasntInteracted(){
        User user = new User("thiago", "cabrera", "thiago@tacs.com","tacs2024");
        Assert.assertFalse(user.hasInteracted());

    }
    private User createTestUser() throws DuplicatedEmailException {
        User user = new User("juan","perez","a@gmail.com","123456");
        user.setId(userService.saveUser(user));
        return user;
    }

}
