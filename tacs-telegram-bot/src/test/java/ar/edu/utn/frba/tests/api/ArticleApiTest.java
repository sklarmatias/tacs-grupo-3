package ar.edu.utn.frba.tests.api;

import org.apache.http.HttpException;
import org.junit.Test;
import org.junitpioneer.jupiter.SetEnvironmentVariable;
import org.tacsbot.model.Article;
import org.tacsbot.model.CostType;

import java.io.IOException;
import java.util.Date;

public class ArticleApiTest {

    private Article article = new Article(
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

    @Test
    @SetEnvironmentVariable(key = "RESOURCE_URL",value = "http://localhost:8080/tacsWSREST")
    public void createArticleTest() throws HttpException, IOException {
//        ArticleApiConnection articleApiConnection = new ArticleApiConnection();
//        Assert.assertNotNull(articleApiConnection.createArticle(article));
    }

}
