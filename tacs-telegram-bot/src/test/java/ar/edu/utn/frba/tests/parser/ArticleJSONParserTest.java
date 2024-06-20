package ar.edu.utn.frba.tests.parser;

import ar.edu.utn.frba.tests.helpers.ModelEqualsHelper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.tacsbot.model.Article;
import org.tacsbot.model.CostType;
import org.tacsbot.parser.article.impl.ArticleJSONParser;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class ArticleJSONParserTest {

    private final String testJSON = """
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

    private final Article article1 = new Article(
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

    private final Article article2 = new Article(
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
        String testJSONList = """
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
        List<Article> convertedArticles = new ArticleJSONParser().parseJSONToArticleList(testJSONList);
        ModelEqualsHelper.assertEquals(article1, convertedArticles.get(0));
        ModelEqualsHelper.assertEquals(article2, convertedArticles.get(1));
    }

    @Test
    public void JSONToArticleTest(){
        Article convertedArticle = new ArticleJSONParser().parseJSONToArticle(testJSON);
        ModelEqualsHelper.assertEquals(article1, convertedArticle);
    }

    @Test
    public void articleToJSONTotalTest() throws IOException {

        String articleJson = new ArticleJSONParser().parseArticleToJSON(article1);

        ObjectMapper mapper = new ObjectMapper();

        JsonNode tree1 = mapper.readTree(articleJson);
        JsonNode tree2 = mapper.readTree(testJSON);

        Assert.assertEquals(tree2, tree1);

    }

    @Test
    public void articleToJSONPerUserTest() throws IOException {

        String articleJson = new ArticleJSONParser().parseArticleToJSON(article1);

        ObjectMapper mapper = new ObjectMapper();

        JsonNode tree1 = mapper.readTree(articleJson);
        JsonNode tree2 = mapper.readTree(testJSON);

        Assert.assertEquals(tree2, tree1);

    }

    @Test
    public void InvalidJSONToArticleTest(){
        String invalidTestJSON = """
                {
                  "name" : "name",
                  "image" : "image.jpg",
                  "link" : "link.com",
                  "deadline" : "2024-05-30",
                  "owner" : "qwerty",
                  "cost" : 200.5,
                  "userGets" : "user gets!",
                  "costType" : "TOTAL",
                  "usersMin" : 2,
                  "usersMax" : 4
                }""";

        Assert.assertThrows(IllegalArgumentException.class,  () -> {
            new ArticleJSONParser().parseJSONToArticle(invalidTestJSON);
        });
    }

    @Test
    public void InvalidJSONToArticleListTest(){
        String invalidTestJSONList = """
                [{
                  "name" : "name",
                  "image" : "image.jpg",
                  "link" : "link.com",
                  "deadline" : "2024-05-30",
                  "owner" : "qwerty",
                  "cost" : 200.5,
                  "userGets" : "user gets!",
                  "costType" : "TOTAL",
                  "usersMin" : 2,
                  "usersMax" : 4
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

        Assert.assertThrows(IllegalArgumentException.class,  () -> {
            new ArticleJSONParser().parseJSONToArticleList(invalidTestJSONList);
        });
    }

}
