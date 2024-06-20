package ar.edu.utn.frba.tests.helpers;

import org.junit.Assert;
import org.tacsbot.model.Article;
import org.tacsbot.model.User;

public class ModelEqualsHelper {

    public static void assertEquals(Article article1, Article article2){
        Assert.assertEquals(article1.getId(), article2.getId());
        Assert.assertEquals(article1.getName(), article2.getName());
        Assert.assertEquals(article1.getImage(), article2.getImage());
        Assert.assertEquals(article1.getLink(), article2.getLink());
        Assert.assertEquals(article1.getCost(), article2.getCost());
        Assert.assertEquals(article1.getCostType(), article2.getCostType());
        Assert.assertEquals(article1.getAnnotations(), article2.getAnnotations());
        Assert.assertEquals(article1.getAnnotationsCounter(), article2.getAnnotationsCounter());
        Assert.assertEquals(article1.getUserGets(), article2.getUserGets());
        Assert.assertEquals(article1.getStatus(), article2.getStatus());
    }

    public static void assertEquals(User user1, User user2){
        Assert.assertEquals(user1.getId(), user2.getId());
        Assert.assertEquals(user1.getName(), user2.getName());
        Assert.assertEquals(user1.getSurname(), user2.getSurname());
        Assert.assertEquals(user1.getEmail(), user2.getEmail());
        Assert.assertEquals(user1.getPass(), user2.getPass());
    }

}
