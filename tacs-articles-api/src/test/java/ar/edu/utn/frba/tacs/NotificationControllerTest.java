package ar.edu.utn.frba.tacs;

import ar.edu.utn.frba.tacs.controller.NotificationController;
import ar.edu.utn.frba.tacs.model.Annotation;
import ar.edu.utn.frba.tacs.model.Article;
import ar.edu.utn.frba.tacs.model.Notification;
import ar.edu.utn.frba.tacs.model.User;
import ar.edu.utn.frba.tacs.service.ArticleService;
import ar.edu.utn.frba.tacs.service.NotificationService;
import ar.edu.utn.frba.tacs.service.UserService;
import com.mongodb.ServerAddress;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.transitions.Mongod;
import de.flapdoodle.embed.mongo.transitions.RunningMongodProcess;
import de.flapdoodle.reverse.TransitionWalker;
import jakarta.ws.rs.core.Response;
import org.junit.*;

import java.util.List;

public class NotificationControllerTest {
    static UserService userService;
    static ArticleService articleService;
    static NotificationService notificationService;
    static TestFunctions testFunctions;
    static NotificationController notificationController;
    static TransitionWalker.ReachedState<RunningMongodProcess> running;
    @BeforeClass
    public static void setUp(){
        running = Mongod.instance().start(Version.Main.V7_0);
        ServerAddress serverAddress = new ServerAddress(String.valueOf(running.current().getServerAddress()));
        userService = new UserService("mongodb://" + serverAddress);
        articleService = new ArticleService("mongodb://" + serverAddress);
        testFunctions = new TestFunctions(userService,articleService);
        notificationService = new NotificationService("mongodb://" + serverAddress);
        notificationController = new NotificationController(notificationService);
    }
    @Before
    public void cleanDB(){
        List<User> usersList = userService.listUsers();
        for(User user : usersList){
            userService.delete(user.getId());
        }
        List<Article> articleList = articleService.listArticles();
        for(Article article : articleList){
            articleService.delete(article.getId());
        }
        List<Notification> notificationList = notificationService.getAllNotifications();
        for(Notification notification : notificationList){
            notificationService.delete(notification.getId());
        }
    }
    @AfterClass
    public static void stop(){
        running.current().stop();

    }
    @Test
    public void testControllerGetPending(){
        User user1 = testFunctions.createTestUser();
        User user2 = testFunctions.createTestUser();
        Article article = testFunctions.createTestArticle(user1.getId());
        List<Notification.NotificationDTO> notifications = notificationController.getPendingNotifications();
        Assert.assertEquals(0,notifications.size());
        Annotation annotation = articleService.signUpUser(article,user2);
        userService.updateAddAnnotation(user2.getId(),annotation);
        notifications = notificationController.getPendingNotifications();
        Assert.assertEquals(1,notifications.size());
        Assert.assertEquals(article.getName(),notifications.get(0).articleName);
        articleService.closeArticle(article);
        notifications= notificationController.getPendingNotifications();
        Assert.assertEquals(3,notifications.size());
    }
    @Test
    public void testControllerMarkNotifiedSuccess(){
        User user1 = testFunctions.createTestUser();
        Article article = testFunctions.createTestArticle(user1.getId());
        articleService.closeArticle(article);
        List<Notification> notifications = notificationService.getPendingNotifications();
        Assert.assertEquals(1,notifications.size());
        Response response = notificationController.markAsNotified(notifications.get(0).getId());
        Assert.assertEquals(200,response.getStatus());
        notifications = notificationService.getPendingNotifications();
        Assert.assertEquals(0,notifications.size());
    }
    @Test
    public void testControllerMarkNotifiedNotFound(){
        Response response = notificationController.markAsNotified("123456789012345678901234");
        Assert.assertEquals(404,response.getStatus());
    }
    @Test
    public void testControllerMarkNotifiedTwiceError(){
        User user1 = testFunctions.createTestUser();
        Article article = testFunctions.createTestArticle(user1.getId());
        articleService.closeArticle(article);
        List<Notification> notifications = notificationService.getPendingNotifications();
        Response response = notificationController.markAsNotified(notifications.get(0).getId());
        response = notificationController.markAsNotified(notifications.get(0).getId());
        Assert.assertEquals(400,response.getStatus());
    }
}
