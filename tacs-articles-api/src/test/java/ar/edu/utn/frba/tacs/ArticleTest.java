package ar.edu.utn.frba.tacs;

import ar.edu.utn.frba.tacs.model.*;
import ar.edu.utn.frba.tacs.service.ArticleService;
import ar.edu.utn.frba.tacs.service.NotificationService;
import ar.edu.utn.frba.tacs.service.UserService;
import com.mongodb.ServerAddress;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.transitions.Mongod;
import de.flapdoodle.embed.mongo.transitions.RunningMongodProcess;
import de.flapdoodle.reverse.TransitionWalker;
import org.junit.*;
import java.util.List;
import java.util.Objects;

public class ArticleTest {
    static ArticleService articleService;
    static UserService userService;
    static NotificationService notificationService;
    static TestFunctions testFunctions;
    static TransitionWalker.ReachedState<RunningMongodProcess> running;

    @BeforeClass
    public static void setUp(){
        running = Mongod.instance().start(Version.Main.V7_0);
        ServerAddress serverAddress = new ServerAddress(String.valueOf(running.current().getServerAddress()));
        articleService = new ArticleService("mongodb://" + serverAddress);
        userService = new UserService("mongodb://" + serverAddress);
        testFunctions = new TestFunctions(userService,articleService);
        notificationService = new NotificationService("mongodb://" + serverAddress);
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
    public void testCreateArticleSuccess(){
        String userId = testFunctions.createTestUser().getId();
        String articleId = testFunctions.createTestArticle(userId).getId();
        Assert.assertNotNull(articleId);
        Article articleFromDB = articleService.getArticle(articleId);
        Assert.assertEquals(articleId, articleFromDB.getId());
        Assert.assertEquals(1,articleService.listArticles().size());
    }
    @Test
    public void testCreateArticleFailNoOwner(){
        Assert.assertThrows(IllegalArgumentException.class, () -> testFunctions.createTestArticle("2"));
    }

    @Test
    public void testCloseSuccessfulArticle(){

        User owner = testFunctions.createTestUser();
        User user1 = testFunctions.createTestUser();
        User user2 = testFunctions.createTestUser();
        Article article = testFunctions.createTestArticle(owner.getId());
        articleService.signUpUser(article,user1);
        articleService.signUpUser(article,user2);
        articleService.closeArticle(article);
        article = articleService.getArticle(article.getId());
        Assert.assertEquals(ArticleStatus.CLOSED_SUCCESS, article.getStatus());
    }

    @Test
    public void testCloseFailedArticle(){
        User owner = testFunctions.createTestUser();
        User user1 = testFunctions.createTestUser();
        Article article = testFunctions.createTestArticle(owner.getId());
        articleService.signUpUser(article,user1);
        articleService.closeArticle(article);
        article = articleService.getArticle(article.getId());
        Assert.assertEquals(ArticleStatus.CLOSED_FAILED, article.getStatus());
        Assert.assertEquals(0,articleService.listOpenArticles().size());
        Assert.assertEquals(2, notificationService.getPendingNotifications().size());
    }

    @Test
    public void testSignUpUserSuccess(){
        User user1 = testFunctions.createTestUser();
        User user2 = testFunctions.createTestUser();
        Article article = testFunctions.createTestArticle(user1.getId());
        Annotation annotation = articleService.signUpUser(article,user2);
        userService.updateAddAnnotation(user2.getId(),annotation);
        article = articleService.getArticle(article.getId());
        Integer expected = 1;
        Assert.assertEquals(expected, article.getAnnotationsCounter());
        Assert.assertEquals(user2.getId(), article.getAnnotations().get(0).getUser().getId());
        List<Annotation> annotationList = articleService.getUsersSignedUp(article.getId());
        Assert.assertEquals(1, annotationList.size());
        List<Notification> notifications = notificationService.getPendingNotifications();
        Assert.assertEquals(1,notifications.size());
        Assert.assertEquals(article.getName(),notifications.get(0).getArticleName());
    }
    @Test
    public void testMultipleSignUpUserSuccess(){
        User user1 = testFunctions.createTestUser();
        User user2 = testFunctions.createTestUser();
        Article article = testFunctions.createTestArticle(user1.getId());
        Article article2 = testFunctions.createTestArticle(user2.getId());
        Annotation annotation = articleService.signUpUser(article,user2);
        userService.updateAddAnnotation(user2.getId(),annotation);
        annotation = articleService.signUpUser(article2,user1);
        userService.updateAddAnnotation(user1.getId(),annotation);
        user1 = userService.getUser(user1.getId());
        user1.setName("pablo");
        userService.updateUser(user1.getId(),user1);
        user1 = userService.getUser(user1.getId());
        user2 = userService.getUser(user2.getId());
        Assert.assertEquals(1,user1.getPostedArticles().size());
        Assert.assertEquals(1,user1.getAnnotations().size());
        Assert.assertEquals(1,user2.getPostedArticles().size());
        Assert.assertEquals(1,user2.getAnnotations().size());

    }

    @Test
    public void testSignUpUserFailUsersMax(){
        User user1 = testFunctions.createTestUser();
        User user2 = testFunctions.createTestUser();
        User user3 = testFunctions.createTestUser();
        User user4 = testFunctions.createTestUser();
        User owner = testFunctions.createTestUser();
        Article article = testFunctions.createTestArticle(owner.getId());
        articleService.signUpUser(article,user1);
        articleService.signUpUser(article,user2);
        articleService.signUpUser(article,user3);
        Assert.assertThrows(IllegalArgumentException.class, () -> articleService.signUpUser(article,user4));

    }
    @Test
    public void testSignUpUserCloseUsersMax(){
        User user1 = testFunctions.createTestUser();
        User user2 = testFunctions.createTestUser();
        User user3 = testFunctions.createTestUser();
        User owner = testFunctions.createTestUser();
        Article article = testFunctions.createTestArticle(owner.getId());
        articleService.signUpUser(article,user1);
        Assert.assertEquals(1,notificationService.getPendingNotifications().size());
        articleService.signUpUser(article,user2);
        Assert.assertEquals(3,notificationService.getPendingNotifications().size());
        articleService.signUpUser(article,user3);
        Assert.assertEquals(10,notificationService.getPendingNotifications().size());
    }
    @Test
    public void testCloseAfterDeadlineNotif(){
        User owner = testFunctions.createTestUser();
        User user1 = testFunctions.createTestUser();
        Article article = testFunctions.createTestArticle(owner.getId());
        articleService.signUpUser(article,user1);
        article.setDeadline(testFunctions.getDate(-1));
        articleService.closeArticle(article);
        Assert.assertEquals(3,notificationService.getPendingNotifications().size());
    }

    @Test
    public void testSignUpUserFailOwnerSignUp(){
        User owner = testFunctions.createTestUser();
        Article article = testFunctions.createTestArticle(owner.getId());
        Assert.assertThrows(IllegalArgumentException.class, () -> articleService.signUpUser(article,owner));
    }

    @Test
    public void testSignUpUserFailUserAlreadySignedUp(){
        User user1 = testFunctions.createTestUser();
        User user2 = testFunctions.createTestUser();
        Article article = testFunctions.createTestArticle(user1.getId());
        articleService.signUpUser(article,user2);
        Assert.assertThrows(IllegalArgumentException.class, () -> articleService.signUpUser(article,user2));

    }

    @Test
    public void testSignUpUserFailClosedArticle(){
        User owner = testFunctions.createTestUser();
        User user1 = testFunctions.createTestUser();
        User user2 = testFunctions.createTestUser();
        Article article = testFunctions.createTestArticle(owner.getId());
        articleService.signUpUser(article,user1);
        articleService.closeArticle(article);
        Assert.assertThrows(IllegalArgumentException.class, () -> articleService.signUpUser(article,user2));
    }

    @Test
    public void testListMyArticles(){
        User user1 = testFunctions.createTestUser();
        User user2 = testFunctions.createTestUser();
        Article article1 = testFunctions.createTestArticle(user1.getId());
        Article article2 = testFunctions.createTestArticle(user2.getId());
        Article article3 = testFunctions.createTestArticle(user2.getId());
        Assert.assertEquals(3,articleService.listArticles().size());
        Assert.assertEquals(1,articleService.listUserArticles(user1.getId()).size());
        Assert.assertEquals(2,articleService.listUserArticles(user2.getId()).size());
    }
    @Test
    public void testEditArticle(){
        User owner = testFunctions.createTestUser();
        Article article = testFunctions.createTestArticle(owner.getId());
        article.setCost(8500.00);
        article.setName("new name");
        articleService.updateArticle(article.getId(),article);
        article = articleService.getArticle(article.getId());
        Assert.assertEquals(Double.valueOf(8500.00),article.getCost());
        Assert.assertEquals("new name",article.getName());
    }
    @Test
    public void testGetArticleNotExistent(){
        Assert.assertNull(articleService.getArticle("123456789012345678901234"));
    }
    @Test
    public void testCloseClosedArticle(){
        User user = testFunctions.createTestUser();
        Article article = testFunctions.createTestArticle(user.getId());
        articleService.closeArticle(article);
        Assert.assertThrows(IllegalArgumentException.class, () -> articleService.closeArticle(article));
    }

    private void saveArticle(Article article){
        article.setId(articleService.saveArticle(article));

    }

    private void assertArticleStatusOnDatabase(ArticleStatus status, Article article){
        Assert.assertEquals(status, articleService.getArticle(article.getId()).getStatus());

    }

    private boolean listContainsArticle(List<Article> articleList, Article article){
        return articleList.stream().anyMatch(a -> Objects.equals(a.getId(), article.getId()));
    }

//    @Test
//    public void expiredArticlesCloseAtDeadline(){
//        User owner = testFunctions.createTestUser();
//
//        User subscriber1 = testFunctions.createTestUser();
//        User subscriber2 = testFunctions.createTestUser();
//
//        Article nonExpiredArticle1 = new Article("article","image","","user get", owner.getId(), testFunctions.getDate(3),2000.00, CostType.PER_USER,2,3);
//        Article nonExpiredArticle2 = new Article("article","image","","user get", owner.getId(), testFunctions.getDate(0),2000.00, CostType.PER_USER,2,3);
//        Article expiredArticle1 = new Article("article","image","","user get", owner.getId(), testFunctions.getDate(0),2000.00, CostType.PER_USER,2,3);
//        Article expiredArticle2 = new Article("article","image","","user get", owner.getId(), testFunctions.getDate(0),2000.00, CostType.PER_USER,2,3);
//
//        expiredArticle1.setDeadline(testFunctions.getDate(-1));
//        expiredArticle2.setDeadline(testFunctions.getDate(-1));
//
//        saveArticle(nonExpiredArticle1);
//        saveArticle(nonExpiredArticle2);
//        saveArticle(expiredArticle1);
//        saveArticle(expiredArticle2);
//
//        articleService.signUpUser(expiredArticle1, subscriber1);
//        articleService.signUpUser(expiredArticle1, subscriber2);
//
//        List<Article> expiredArticles = articleService.closeExpiredArticles();
//
//        assertArticleStatusOnDatabase(ArticleStatus.CLOSED_SUCCESS, expiredArticle1);
//        assertArticleStatusOnDatabase(ArticleStatus.CLOSED_FAILED, expiredArticle2);
//        assertArticleStatusOnDatabase(ArticleStatus.OPEN, nonExpiredArticle1);
//        assertArticleStatusOnDatabase(ArticleStatus.OPEN, nonExpiredArticle2);
//
//        Assert.assertTrue(listContainsArticle(expiredArticles, expiredArticle1));
//        Assert.assertTrue(listContainsArticle(expiredArticles, expiredArticle2));
//        Assert.assertFalse(listContainsArticle(expiredArticles, nonExpiredArticle1));
//        Assert.assertFalse(listContainsArticle(expiredArticles, nonExpiredArticle2));
//
//    }

}
