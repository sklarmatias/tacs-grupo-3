package ar.edu.utn.frba.tests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.tacsbot.model.Article;
import org.tacsbot.model.CostType;
import org.tacsbot.service.parser.article.impl.ArticleJSONParser;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class ArticleJSONParserTest {

    private String testJSON = """
                {
                  "name" : "name",
                  "image" : "image.jpg",
                  "link" : "link.com",
                  "deadline" : "2024-05-30",
                  "owner" : "qwerty",
                  "cost" : 200.5,
                  "user_gets" : "user gets!",
                  "cost_type" : "TOTAL",
                  "users_min" : 2,
                  "users_max" : 4
                }""";

    private String testJSONList = """
                [{
                  "name" : "name",
                  "image" : "image.jpg",
                  "link" : "link.com",
                  "deadline" : "2024-05-30",
                  "owner" : "qwerty",
                  "cost" : 200.5,
                  "user_gets" : "user gets!",
                  "cost_type" : "TOTAL",
                  "users_min" : 2,
                  "users_max" : 4
                },
                {
                  "name" : "name1",
                  "image" : "image1.jpg",
                  "link" : "link1.com",
                  "deadline" : "2024-09-30",
                  "owner" : "qwerty1",
                  "cost" : 2001.5,
                  "user_gets" : "user gets1!",
                  "cost_type" : "TOTAL",
                  "users_min" : 1,
                  "users_max" : 2
                }]""";

    private Article article1 = new Article(
            null,
            "name",
            "image.jpg",
            "link.com",
            "user gets!",
            null,
            new Date(1717038000000L), //2024-05-30 00:00:00
            "qwerty",
            null,
            null,
            null,
            200.5,
            CostType.TOTAL,
            2,
            4
    );

    private Article article2 = new Article(
            null,
            "name1",
            "image1.jpg",
            "link1.com",
            "user gets1!",
            null,
            new Date(1727665200L), //2024-09-30 00:00:00
            "qwerty1",
            null,
            null,
            null,
            2001.5,
            CostType.TOTAL,
            1,
            2
    );

    @Test
    public void JSONToArticlesTest(){

        // Assert
        List<Article> convertedArticles = ArticleJSONParser.parseJSONToArticleList(testJSONList);
        assertArticlesEqual(article1, convertedArticles.get(0));
        assertArticlesEqual(article2, convertedArticles.get(1));
    }

    private void assertArticlesEqual(Article article1, Article article2){
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

    @Test
    public void JSONToArticleTest(){
        Article convertedArticle = ArticleJSONParser.parseJSONToArticle(testJSON);
        assertArticlesEqual(article1, convertedArticle);
    }

    @Test
    public void articleToJSONTotalTest() throws IOException {

        String articleJson = ArticleJSONParser.parseArticleToJSON(article1);

        ObjectMapper mapper = new ObjectMapper();

        JsonNode tree1 = mapper.readTree(articleJson);
        JsonNode tree2 = mapper.readTree(testJSON);

        Assert.assertEquals(tree2, tree1);

    }

    @Test
    public void articleToJSONPerUserTest() throws IOException {

        String articleJson = ArticleJSONParser.parseArticleToJSON(article1);

        ObjectMapper mapper = new ObjectMapper();

        JsonNode tree1 = mapper.readTree(articleJson);
        JsonNode tree2 = mapper.readTree(testJSON);

        Assert.assertEquals(tree2, tree1);

    }

}
