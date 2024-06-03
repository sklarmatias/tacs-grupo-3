package ar.edu.utn.frba.tacs;

import ar.edu.utn.frba.tacs.model.*;
import ar.edu.utn.frba.tacs.service.ArticleService;
import ar.edu.utn.frba.tacs.service.UserService;
import com.mongodb.ServerAddress;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.transitions.Mongod;
import de.flapdoodle.embed.mongo.transitions.RunningMongodProcess;
import de.flapdoodle.reverse.TransitionWalker;
import org.junit.*;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class ArticleTest {
    static ArticleService articleService;
    static UserService userService;
    @Before
    public void setUp(){
        TransitionWalker.ReachedState<RunningMongodProcess> running = Mongod.instance().start(Version.Main.V7_0);
        ServerAddress serverAddress = new ServerAddress(String.valueOf(running.current().getServerAddress()));
        articleService = new ArticleService("mongodb://" + serverAddress);
        userService = new UserService("mongodb://" + serverAddress);
    }
    @Test
    public void testCreateArticleSuccess(){
        String userId = createTestUser().getId();
        String articleId = createTestArticle(userId).getId();
        Assert.assertNotNull(articleId);
        Article articleFromDB = articleService.getArticle(articleId);
        Assert.assertEquals(articleId, articleFromDB.getId());
        Assert.assertEquals(1,articleService.listArticles().size());
    }
    @Test
    public void testCreateArticleFailNoOwner(){
        Assert.assertThrows(IllegalArgumentException.class, () -> createTestArticle("2"));
    }

    @Test
    public void testCloseSuccessfulArticle(){

        User owner = createTestUser();
        User user1 = createTestUser();
        User user2 = createTestUser();
        Article article = createTestArticle(owner.getId());
        articleService.signUpUser(article,user1);
        articleService.signUpUser(article,user2);
        articleService.closeArticle(article);
        article = articleService.getArticle(article.getId());
        Assert.assertEquals(ArticleStatus.CLOSED_SUCCESS, article.getStatus());
    }

    @Test
    public void testCloseFailedArticle(){
        User owner = createTestUser();
        User user1 = createTestUser();
        Article article = createTestArticle(owner.getId());
        articleService.signUpUser(article,user1);
        articleService.closeArticle(article);
        article = articleService.getArticle(article.getId());
        Assert.assertEquals(ArticleStatus.CLOSED_FAILED, article.getStatus());
        Assert.assertEquals(0,articleService.listArticles().size());
    }

    @Test
    public void testSignUpUserSuccess(){
        User user1 = createTestUser();
        User user2 = createTestUser();
        Article article = createTestArticle(user1.getId());
        articleService.signUpUser(article,user2);
        article = articleService.getArticle(article.getId());
        Integer expected = 1;
        Assert.assertEquals(expected, article.getAnnotationsCounter());
        Assert.assertEquals(user2.getId(), article.getAnnotations().get(0).getUser().getId());
    }

    @Test
    public void testSignUpUserFailUsersMax(){
        User user1 = createTestUser();
        User user2 = createTestUser();
        User user3 = createTestUser();
        User user4 = createTestUser();
        User owner = createTestUser();
        Article article = createTestArticle(owner.getId());
        articleService.signUpUser(article,user1);
        articleService.signUpUser(article,user2);
        articleService.signUpUser(article,user3);
        Assert.assertThrows(IllegalArgumentException.class, () -> articleService.signUpUser(article,user4));

    }

    @Test
    public void testSignUpUserFailOwnerSignUp(){
        User owner = createTestUser();
        Article article = createTestArticle(owner.getId());
        Assert.assertThrows(IllegalArgumentException.class, () -> articleService.signUpUser(article,owner));
    }

    @Test
    public void testSignUpUserFailUserAlreadySignedUp(){
        User user1 = createTestUser();
        User user2 = createTestUser();
        Article article = createTestArticle(user1.getId());
        articleService.signUpUser(article,user2);
        Assert.assertThrows(IllegalArgumentException.class, () -> articleService.signUpUser(article,user2));

    }

    @Test
    public void testSignUpUserFailClosedArticle(){
        User owner = createTestUser();
        User user1 = createTestUser();
        User user2 = createTestUser();
        Article article = createTestArticle(owner.getId());
        articleService.signUpUser(article,user1);
        articleService.closeArticle(article);
        Assert.assertThrows(IllegalArgumentException.class, () -> articleService.signUpUser(article,user2));
    }


    private User createTestUser(){
        User user = new User("juan","perez",random() + "@gmail.com","123456");
        user.setId(userService.saveUser(user));
        return user;
    }
    private String random() {
        byte[] array = new byte[4];
        new Random().nextBytes(array);
        return new String(array, StandardCharsets.UTF_8);

    }

    private Article createTestArticle(String userId) throws IllegalArgumentException{
        Date dt = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(dt);
        c.add(Calendar.DATE, 2);
        dt = c.getTime();
        Article article = new Article("article","image","","user get",userId,dt,2000.00, CostType.PER_USER,2,3);
        article.setId(articleService.saveArticle(article));
        userService.updateUserAddArticle(userId,article);
        return article;
    }
}
