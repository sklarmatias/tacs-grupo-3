package ar.edu.utn.frba.tacs;

import ar.edu.utn.frba.tacs.model.*;
import ar.edu.utn.frba.tacs.repository.MongoDBConnector;
import ar.edu.utn.frba.tacs.repository.articles.impl.MongoArticlesRepository;
import ar.edu.utn.frba.tacs.repository.user.impl.MongoUsersRepository;
import ar.edu.utn.frba.tacs.service.ArticleService;
import ar.edu.utn.frba.tacs.service.UserService;
import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ArticleTest {
    ArticleService articleService = new ArticleService();
    UserService userService = new UserService();
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
        articleService.signUpUser(article,user1.convertToDTO());
        articleService.signUpUser(article,user2.convertToDTO());
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
        articleService.signUpUser(article,user1.convertToDTO());
        articleService.closeArticle(article);
        article = articleService.getArticle(article.getId());
        Assert.assertEquals(ArticleStatus.CLOSED_FAILED, article.getStatus());
        articleService.clearArticle(article.getId());
        userService.cleanUser(owner.getId());
        userService.cleanUser(user1.getId());
    }

    @Test
    public void testSignUpUserSuccess(){
        User user1 = createTestUser();
        User user2 = createTestUser();
        Article article = createTestArticle(user1.getId());
        articleService.signUpUser(article,user2.convertToDTO());
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
        articleService.signUpUser(article,user1.convertToDTO());
        articleService.signUpUser(article,user2.convertToDTO());
        articleService.signUpUser(article,user3.convertToDTO());
        Assert.assertThrows(IllegalArgumentException.class, () -> articleService.signUpUser(article,user4.convertToDTO()));

        articleService.clearArticle(article.getId());
        userService.cleanUser(user1.getId());
        userService.cleanUser(user2.getId());
        userService.cleanUser(user3.getId());
        userService.cleanUser(owner.getId());

    }

    @Test
    public void testSignUpUserFailOwnerSignUp(){
        User owner = createTestUser();
        Article article = createTestArticle(owner.getId());
        Assert.assertThrows(IllegalArgumentException.class, () -> articleService.signUpUser(article,owner.convertToDTO()));
        articleService.clearArticle(article.getId());
        userService.cleanUser(owner.getId());
    }

    @Test
    public void testSignUpUserFailUserAlreadySignedUp(){
        User user1 = createTestUser();
        User user2 = createTestUser();
        Article article = createTestArticle(user1.getId());
        articleService.signUpUser(article,user2.convertToDTO());
        Assert.assertThrows(IllegalArgumentException.class, () -> articleService.signUpUser(article,user2.convertToDTO()));
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
        articleService.signUpUser(article,user1.convertToDTO());
        articleService.closeArticle(article);
        Assert.assertThrows(IllegalArgumentException.class, () -> articleService.signUpUser(article,user2.convertToDTO()));
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
