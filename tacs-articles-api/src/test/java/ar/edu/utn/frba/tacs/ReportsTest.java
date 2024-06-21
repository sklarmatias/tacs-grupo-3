package ar.edu.utn.frba.tacs;

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
import org.checkerframework.checker.units.qual.A;
import org.junit.*;

import java.util.List;

public class ReportsTest {
    static ArticleService articleService;
    static UserService userService;
    static TestFunctions testFunctions;
    static ReportService reportsService;
    static TransitionWalker.ReachedState<RunningMongodProcess> running;

    @BeforeClass
    public static void setUp(){
        running = Mongod.instance().start(Version.Main.V7_0);
        ServerAddress serverAddress = new ServerAddress(String.valueOf(running.current().getServerAddress()));
        articleService = new ArticleService("mongodb://" + serverAddress);
        userService = new UserService("mongodb://" + serverAddress);
        testFunctions = new TestFunctions(userService,articleService);
        reportsService = new ReportService("mongodb://" + serverAddress);
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
        Assert.assertEquals(0,reportsService.getUsersCount());
        User user = testFunctions.createTestUser();
        Assert.assertEquals(1,reportsService.getUsersCount());
        User user2 = testFunctions.createTestUser();
        Assert.assertEquals(2,reportsService.getUsersCount());
    }
    @Test
    public void testReportArticles(){
        Assert.assertEquals(0,reportsService.countArticles());
        User user = testFunctions.createTestUser();
        Article article = testFunctions.createTestArticle(user.getId());
        Assert.assertEquals(1,reportsService.countArticles());
        Article article2 = testFunctions.createTestArticle(user.getId());
        Assert.assertEquals(2,reportsService.countArticles());
    }
    @Test
    public void testReportEngagedUsers(){
        Assert.assertEquals(0,reportsService.getEngagedUsers());
        User user = testFunctions.createTestUser();
        Assert.assertEquals(0,reportsService.getEngagedUsers());
        Article article = testFunctions.createTestArticle(user.getId());
        Assert.assertEquals(1,reportsService.getEngagedUsers());
        User user2 = testFunctions.createTestUser();
        Assert.assertEquals(1,reportsService.getEngagedUsers());
        Annotation annotation = articleService.signUpUser(article, user2);
        userService.updateAddAnnotation(user2.getId(),annotation);
        Assert.assertEquals(2,reportsService.getEngagedUsers());
    }
    @Test
    public void testFailedArticles(){
        Assert.assertEquals(0,reportsService.countFailedArticles());
        User user = testFunctions.createTestUser();
        Article article = testFunctions.createTestArticle(user.getId());
        Article article2 = testFunctions.createTestArticle(user.getId());
        Assert.assertEquals(0,reportsService.countFailedArticles());
        article = articleService.closeArticle(article);
        List<Article> articles = articleService.listArticles();
        Assert.assertEquals(1,reportsService.countFailedArticles());
        articleService.closeArticle(article2);
        Assert.assertEquals(2,reportsService.countFailedArticles());

    }
    @Test
    public void testSuccessfulArticles(){
        Assert.assertEquals(0,reportsService.countSuccessfulArticles());
        User user = testFunctions.createTestUser();
        User user2 = testFunctions.createTestUser();
        User user3 = testFunctions.createTestUser();
        User user4 = testFunctions.createTestUser();
        Article article = testFunctions.createTestArticle(user.getId());
        Article article2 = testFunctions.createTestArticle(user.getId());
        Assert.assertEquals(0,reportsService.countSuccessfulArticles());
        Annotation annotation = articleService.signUpUser(article, user2);
        userService.updateAddAnnotation(user2.getId(),annotation);
        annotation = articleService.signUpUser(article, user3);
        userService.updateAddAnnotation(user3.getId(),annotation);
        annotation = articleService.signUpUser(article, user4);
        userService.updateAddAnnotation(user4.getId(),annotation);
        Assert.assertEquals(1,reportsService.countSuccessfulArticles());
        annotation = articleService.signUpUser(article2, user2);
        userService.updateAddAnnotation(user2.getId(),annotation);
        annotation = articleService.signUpUser(article2, user3);
        userService.updateAddAnnotation(user3.getId(),annotation);
        articleService.closeArticle(article2);
        Assert.assertEquals(2,reportsService.countSuccessfulArticles());

    }
}
