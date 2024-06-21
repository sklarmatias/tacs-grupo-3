package ar.edu.utn.frba.tacs;

import ar.edu.utn.frba.tacs.exception.DuplicatedEmailException;
import ar.edu.utn.frba.tacs.model.Article;
import ar.edu.utn.frba.tacs.model.CostType;
import ar.edu.utn.frba.tacs.model.User;
import ar.edu.utn.frba.tacs.service.ArticleService;
import ar.edu.utn.frba.tacs.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class TestFunctions {
    UserService userService;
    ArticleService articleService;

    public TestFunctions(UserService userService, ArticleService articleService){
        this.articleService = articleService;
        this.userService = userService;
    }

    public User createTestUser() throws DuplicatedEmailException {
        User user = new User("juan","perez",random() + "@gmail.com","123456");
        user.setId(userService.saveUser(user));
        return user;
    }
    public String random() {
        byte[] array = new byte[4];
        new Random().nextBytes(array);
        return new String(array, StandardCharsets.UTF_8);

    }

    public Article createTestArticle(String userId) throws IllegalArgumentException{
        Article article = new Article("article","image","","user get",userId,getDate(2),2000.00, CostType.PER_USER,2,3);
        article.setId(articleService.saveArticle(article));
        userService.updateUserAddArticle(userId,article);
        return article;
    }
    public Date getDate(int days){
        Date dt = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(dt);
        c.add(Calendar.DATE, days);
        dt = c.getTime();
        return dt;
    }
}
