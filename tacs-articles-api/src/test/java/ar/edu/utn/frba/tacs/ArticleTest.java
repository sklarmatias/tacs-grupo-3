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

import java.util.Calendar;
import java.util.Date;

public class ArticleTest {
    static ArticleService articleService;
    static UserService userService;
    @BeforeClass
    public static void setUp(){
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
        articleService.clearArticle(articleId);
        userService.cleanUser(userId);
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
        articleService.clearArticle(article.getId());
        userService.cleanUser(owner.getId());
        userService.cleanUser(user1.getId());
        userService.cleanUser(user2.getId());
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
        articleService.clearArticle(article.getId());
        userService.cleanUser(owner.getId());
        userService.cleanUser(user1.getId());
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
        articleService.clearArticle(article.getId());
        userService.cleanUser(user1.getId());
        userService.cleanUser(user2.getId());
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

        articleService.clearArticle(article.getId());
        userService.cleanUser(user1.getId());
        userService.cleanUser(user2.getId());
        userService.cleanUser(user3.getId());
        userService.cleanUser(user4.getId());
        userService.cleanUser(owner.getId());

    }

    @Test
    public void testSignUpUserFailOwnerSignUp(){
        User owner = createTestUser();
        Article article = createTestArticle(owner.getId());
        Assert.assertThrows(IllegalArgumentException.class, () -> articleService.signUpUser(article,owner));
        articleService.clearArticle(article.getId());
        userService.cleanUser(owner.getId());
    }

    @Test
    public void testSignUpUserFailUserAlreadySignedUp(){
        User user1 = createTestUser();
        User user2 = createTestUser();
        Article article = createTestArticle(user1.getId());
        articleService.signUpUser(article,user2);
        Assert.assertThrows(IllegalArgumentException.class, () -> articleService.signUpUser(article,user2));
        articleService.clearArticle(article.getId());
        userService.cleanUser(user1.getId());
        userService.cleanUser(user2.getId());

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
        articleService.clearArticle(article.getId());
        userService.cleanUser(owner.getId());
        userService.cleanUser(user1.getId());
        userService.cleanUser(user2.getId());
    }


    private User createTestUser(){
        User user = new User("juan","perez","jp@gmail.com","123456");
        user.setId(userService.saveUser(user));
        return user;
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
