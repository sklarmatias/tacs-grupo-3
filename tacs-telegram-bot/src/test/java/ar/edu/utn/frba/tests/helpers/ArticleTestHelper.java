package ar.edu.utn.frba.tests.helpers;

import org.junit.Assert;
import org.tacsbot.model.Article;

public class ArticleTestHelper {

    public static void assertArticlesEqual(Article article1, Article article2){
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

}
