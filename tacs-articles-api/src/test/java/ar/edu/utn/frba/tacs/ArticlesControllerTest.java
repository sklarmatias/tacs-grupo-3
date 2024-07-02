package ar.edu.utn.frba.tacs;

import ar.edu.utn.frba.tacs.controller.ArticleController;
import ar.edu.utn.frba.tacs.model.*;
import ar.edu.utn.frba.tacs.service.ArticleService;
import ar.edu.utn.frba.tacs.service.UserService;
import com.mongodb.ServerAddress;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.transitions.Mongod;
import de.flapdoodle.embed.mongo.transitions.RunningMongodProcess;
import de.flapdoodle.reverse.TransitionWalker;
import jakarta.ws.rs.core.Response;
import org.junit.*;

import java.util.List;

public class ArticlesControllerTest {
    static ArticleService articleService;
    static UserService userService;
    static TestFunctions testFunctions;
    static ArticleController articleController;
    static TransitionWalker.ReachedState<RunningMongodProcess> running;

    @BeforeClass
    public static void setUp(){
        running = Mongod.instance().start(Version.Main.V7_0);
        ServerAddress serverAddress = new ServerAddress(String.valueOf(running.current().getServerAddress()));
        articleService = new ArticleService("mongodb://" + serverAddress);
        userService = new UserService("mongodb://" + serverAddress);
        testFunctions = new TestFunctions(userService,articleService);
        articleController = new ArticleController(articleService,userService);
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
    public void testControllerListArticlesWithoutUser(){
        User user = testFunctions.createTestUser();
        Assert.assertEquals(0,articleController.listArticles(null).size());
        Article article = testFunctions.createTestArticle(user.getId());
        Assert.assertEquals(1,articleController.listArticles(null).size());
        articleService.closeArticle(article);
        Assert.assertEquals(0,articleController.listArticles(null).size());
    }
    @Test
    public void testControllerListArticlesWithUser(){
        User user = testFunctions.createTestUser();
        Assert.assertEquals(0,articleController.listArticles(user.getId()).size());
        Article article = testFunctions.createTestArticle(user.getId());
        Assert.assertEquals(1,articleController.listArticles(user.getId()).size());
        articleService.closeArticle(article);
        Assert.assertEquals(0,articleController.listArticles(null).size());
        Assert.assertEquals(1,articleController.listArticles(user.getId()).size());
    }

    @Test
    public void testControllerCreateArticle(){
        String userId = testFunctions.createTestUser().getId();
        String articleId = testFunctions.createTestArticle(userId).getId();
        Assert.assertEquals(articleId,articleController.getArticle(articleId).id);
    }
    @Test
    public void testControllerCreateArticleSuccess(){
        User user = testFunctions.createTestUser();
        Article article = new Article("article","image","","user get", user.getId(), testFunctions.getDate(2),2000.00, CostType.PER_USER,2,3);
        Response response = articleController.saveArticle(user.getId(), article);
        Assert.assertEquals(Response.Status.CREATED.getStatusCode(),response.getStatus());
        Article articleResponse = (Article)response.getEntity();
        Assert.assertEquals(article.getName(),articleResponse.getName());
    }
    @Test
    public void testControllerCreateArticleNoUser(){
        User user = testFunctions.createTestUser();
        Article article = new Article("article","image","","user get", user.getId(), testFunctions.getDate(2),2000.00, CostType.PER_USER,2,3);
        Response response = articleController.saveArticle(null, article);
        Assert.assertEquals(Response.Status.FORBIDDEN.getStatusCode(),response.getStatus());
    }
    @Test
    public void testControllerCreateArticleWrongUser(){
        User user = testFunctions.createTestUser();
        Article article = new Article("article","image","","user get", user.getId(), testFunctions.getDate(2),2000.00, CostType.PER_USER,2,3);
        Response response = articleController.saveArticle("123456789012345678901234", article);
        Assert.assertEquals(Response.Status.FORBIDDEN.getStatusCode(),response.getStatus());
    }
    @Test
    public void testControllerSignUpSuccess(){
        User user = testFunctions.createTestUser();
        User user2 = testFunctions.createTestUser();
        Article article = testFunctions.createTestArticle(user.getId());
        Response response = articleController.signUpUser(article.getId(), user2.getId());
        Assert.assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
    }
    @Test
    public void testControllerSignUpUserNull(){
        User user = testFunctions.createTestUser();
        User user2 = testFunctions.createTestUser();
        Article article = testFunctions.createTestArticle(user.getId());
        Response response = articleController.signUpUser(article.getId(), null);
        Assert.assertEquals(Response.Status.FORBIDDEN.getStatusCode(),response.getStatus());
    }

    @Test
    public void testControllerSignUpUserNotFound(){
        User user = testFunctions.createTestUser();
        User user2 = testFunctions.createTestUser();
        Article article = testFunctions.createTestArticle(user.getId());
        Response response = articleController.signUpUser(article.getId(), "123456789012345678901234");
        Assert.assertEquals(Response.Status.FORBIDDEN.getStatusCode(),response.getStatus());
    }
    @Test
    public void testControllerSignUpFailedAlreadyClosed(){
        User user = testFunctions.createTestUser();
        User user2 = testFunctions.createTestUser();
        Article article = testFunctions.createTestArticle(user.getId());
        articleService.closeArticle(article);
        Response response = articleController.signUpUser(article.getId(), user2.getId());
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),response.getStatus());
        Assert.assertEquals("1",response.getEntity());
    }
    @Test
    public void testControllerSignUpFailedOwner(){
        User user = testFunctions.createTestUser();
        Article article = testFunctions.createTestArticle(user.getId());
        Response response = articleController.signUpUser(article.getId(), user.getId());
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),response.getStatus());
        Assert.assertEquals("2",response.getEntity());
    }
    @Test
    public void testControllerSignUpFailedAlreadySigned(){
        User user = testFunctions.createTestUser();
        User user2 = testFunctions.createTestUser();
        Article article = testFunctions.createTestArticle(user.getId());
        Annotation annotation = articleService.signUpUser(article, user2);
        userService.updateAddAnnotation(user2.getId(),annotation);
        Response response = articleController.signUpUser(article.getId(), user2.getId());
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),response.getStatus());
        Assert.assertEquals("3",response.getEntity());
    }
    @Test
    public void testControllerSignUpFailedArticleNotFound(){
        User user = testFunctions.createTestUser();
        User user2 = testFunctions.createTestUser();
        Article article = testFunctions.createTestArticle(user.getId());
        Response response = articleController.signUpUser("123456789012345678901234", user2.getId());
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),response.getStatus());
        Assert.assertEquals("4",response.getEntity());
    }
    @Test
    public void testControllerCloseSuccess(){
        User user = testFunctions.createTestUser();
        Article article = testFunctions.createTestArticle(user.getId());
        Response response = articleController.closeArticle(article.getId(),user.getId());
        Assert.assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
    }
    @Test
    public void testControllerCloseUserNull(){
        User user = testFunctions.createTestUser();
        Article article = testFunctions.createTestArticle(user.getId());
        Response response = articleController.closeArticle(article.getId(),null);
        Assert.assertEquals(Response.Status.FORBIDDEN.getStatusCode(),response.getStatus());
    }
    @Test
    public void testControllerCloseUserNotOwner(){
        User user = testFunctions.createTestUser();
        User user2 = testFunctions.createTestUser();
        Article article = testFunctions.createTestArticle(user.getId());
        Response response = articleController.closeArticle(article.getId(),user2.getId());
        Assert.assertEquals(Response.Status.FORBIDDEN.getStatusCode(),response.getStatus());
    }
    @Test
    public void testControllerCloseFailed(){
        User user = testFunctions.createTestUser();
        Article article = testFunctions.createTestArticle(user.getId());
        articleService.closeArticle(article);
        Response response = articleController.closeArticle(article.getId(),user.getId());
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),response.getStatus());
        Assert.assertEquals("1",response.getEntity());
    }
    @Test
    public void testControllerGetSignedUpSuccess(){
        User user = testFunctions.createTestUser();
        User user2 = testFunctions.createTestUser();
        Article article = testFunctions.createTestArticle(user.getId());
        Annotation annotation = articleService.signUpUser(article, user2);
        userService.updateAddAnnotation(user2.getId(),annotation);
        Response response = articleController.getUsersSignedUp(article.getId());
        Assert.assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
        List<Annotation> list = (List<Annotation>)response.getEntity();
        Assert.assertEquals(1,list.size());
    }
    @Test
    public void testControllerGetSignedUpWrongArticle(){
        User user = testFunctions.createTestUser();
        Article article = testFunctions.createTestArticle(user.getId());
        Response response = articleController.getUsersSignedUp("123456789012345678901234");
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),response.getStatus());
    }
    @Test
    public void testControllerUpdateSuccess(){
        User user = testFunctions.createTestUser();
        Article article = testFunctions.createTestArticle(user.getId());
        article.setName("new name");
        Response response = articleController.updateArticle(user.getId(), article.getId(), article);
        Assert.assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
    }
    @Test
    public void testControllerUpdateUserNull(){
        User user = testFunctions.createTestUser();
        Article article = testFunctions.createTestArticle(user.getId());
        article.setName("new name");
        Response response = articleController.updateArticle(null, article.getId(), article);
        Assert.assertEquals(Response.Status.FORBIDDEN.getStatusCode(),response.getStatus());
    }
    @Test
    public void testControllerUpdateUserNotOwner(){
        User user = testFunctions.createTestUser();
        User user2 = testFunctions.createTestUser();
        Article article = testFunctions.createTestArticle(user.getId());
        article.setName("new name");
        Response response = articleController.updateArticle(user2.getId(), article.getId(), article);
        Assert.assertEquals(Response.Status.FORBIDDEN.getStatusCode(),response.getStatus());
    }
}
