package ar.edu.utn.frba.tacs;

import ar.edu.utn.frba.tacs.controller.ReportsController;
import ar.edu.utn.frba.tacs.model.Annotation;
import ar.edu.utn.frba.tacs.model.Article;
import ar.edu.utn.frba.tacs.model.User;
import ar.edu.utn.frba.tacs.service.ArticleService;
import ar.edu.utn.frba.tacs.service.ReportService;
import ar.edu.utn.frba.tacs.service.UserService;
import com.mongodb.ServerAddress;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.transitions.Mongod;
import de.flapdoodle.embed.mongo.transitions.RunningMongodProcess;
import de.flapdoodle.reverse.TransitionWalker;
import org.junit.*;

import java.util.List;

public class ReportsControllerTest {
    static UserService userService;
    static ArticleService articleService;
    static ReportService reportsService;
    static TestFunctions testFunctions;
    static ReportsController reportsController;
    static TransitionWalker.ReachedState<RunningMongodProcess> running;
    @BeforeClass
    public static void setUp(){
        running = Mongod.instance().start(Version.Main.V7_0);
        ServerAddress serverAddress = new ServerAddress(String.valueOf(running.current().getServerAddress()));
        userService = new UserService("mongodb://" + serverAddress);
        articleService = new ArticleService("mongodb://" + serverAddress);
        testFunctions = new TestFunctions(userService,articleService);
        reportsService = new ReportService("mongodb://" + serverAddress);
        reportsController = new ReportsController(reportsService);
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
    }
    @AfterClass
    public static void stop(){
        running.current().stop();

    }
    @Test
    public void testReportUsers(){
        Assert.assertEquals(0,reportsController.getUsersCount());
        User user = testFunctions.createTestUser();
        Assert.assertEquals(1,reportsController.getUsersCount());
        User user2 = testFunctions.createTestUser();
        Assert.assertEquals(2,reportsController.getUsersCount());
    }
    @Test
    public void testReportArticles(){
        Assert.assertEquals(0,reportsController.countArticles());
        User user = testFunctions.createTestUser();
        Article article = testFunctions.createTestArticle(user.getId());
        Assert.assertEquals(1,reportsController.countArticles());
        Article article2 = testFunctions.createTestArticle(user.getId());
        Assert.assertEquals(2,reportsController.countArticles());
    }
    @Test
    public void testReportEngagedUsers(){
        Assert.assertEquals(0,reportsController.getEngagedUsers());
        User user = testFunctions.createTestUser();
        Assert.assertEquals(0,reportsController.getEngagedUsers());
        Article article = testFunctions.createTestArticle(user.getId());
        Assert.assertEquals(1,reportsController.getEngagedUsers());
        User user2 = testFunctions.createTestUser();
        Assert.assertEquals(1,reportsController.getEngagedUsers());
        Annotation annotation = articleService.signUpUser(article, user2);
        userService.updateAddAnnotation(user2.getId(),annotation);
        Assert.assertEquals(2,reportsController.getEngagedUsers());
    }
    @Test
    public void testFailedArticles(){
        Assert.assertEquals(0,reportsController.countFailedArticles());
        User user = testFunctions.createTestUser();
        Article article = testFunctions.createTestArticle(user.getId());
        Article article2 = testFunctions.createTestArticle(user.getId());
        Assert.assertEquals(0,reportsController.countFailedArticles());
        articleService.closeArticle(article);
        List<Article> articles = articleService.listArticles();
        Assert.assertEquals(1,reportsController.countFailedArticles());
        articleService.closeArticle(article2);
        Assert.assertEquals(2,reportsController.countFailedArticles());

    }
    @Test
    public void testSuccessfulArticles(){
        Assert.assertEquals(0,reportsController.countSuccessfulArticles());
        User user = testFunctions.createTestUser();
        User user2 = testFunctions.createTestUser();
        User user3 = testFunctions.createTestUser();
        User user4 = testFunctions.createTestUser();
        Article article = testFunctions.createTestArticle(user.getId());
        Article article2 = testFunctions.createTestArticle(user.getId());
        Assert.assertEquals(0,reportsController.countSuccessfulArticles());
        Annotation annotation = articleService.signUpUser(article, user2);
        userService.updateAddAnnotation(user2.getId(),annotation);
        annotation = articleService.signUpUser(article, user3);
        userService.updateAddAnnotation(user3.getId(),annotation);
        annotation = articleService.signUpUser(article, user4);
        userService.updateAddAnnotation(user4.getId(),annotation);
        Assert.assertEquals(1,reportsController.countSuccessfulArticles());
        annotation = articleService.signUpUser(article2, user2);
        userService.updateAddAnnotation(user2.getId(),annotation);
        annotation = articleService.signUpUser(article2, user3);
        userService.updateAddAnnotation(user3.getId(),annotation);
        articleService.closeArticle(article2);
        Assert.assertEquals(2,reportsController.countSuccessfulArticles());

    }
}
