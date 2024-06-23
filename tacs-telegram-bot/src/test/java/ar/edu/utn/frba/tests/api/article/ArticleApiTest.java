package ar.edu.utn.frba.tests.api.article;

import ar.edu.utn.frba.tests.helpers.ModelEqualsHelper;
import org.apache.http.HttpException;
import org.junit.Assert;
import org.junit.Test;
import org.tacsbot.api.article.impl.ArticleApiConnection;
import org.tacsbot.api.article.impl.ArticleHttpConnector;
import org.tacsbot.model.Annotation;
import org.tacsbot.model.Article;
import org.tacsbot.model.CostType;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.util.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ArticleApiTest {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
    public void createArticleSuccessTest() throws HttpException, IOException, URISyntaxException, InterruptedException {
        // mock HttpResponse object
        HttpResponse<String> httpResponse = mock(HttpResponse.class);
        when(httpResponse.body()).thenReturn("qwerty");
        when(httpResponse.statusCode()).thenReturn(201);
        // mock ArticleHttpConnector object
        ArticleHttpConnector articleHttpConnector = mock(ArticleHttpConnector.class);
        when(articleHttpConnector.createArticleConnector(anyString(), anyString())).thenReturn(httpResponse);
        // set mocked ArticleHttpConnector on ArticleApiConnection
        ArticleApiConnection articleApiConnection = new ArticleApiConnection();
        articleApiConnection.setArticleHttpConnector(articleHttpConnector);
        // assert
        Assert.assertEquals("qwerty", articleApiConnection.createArticle(article));
    }

    @Test
    public void createArticleWrongStatusCodeTest() throws IOException, URISyntaxException, InterruptedException {
        // mock HttpResponse object
        HttpResponse<String> httpResponse = mock(HttpResponse.class);
        when(httpResponse.statusCode()).thenReturn(403);
        // mock ArticleHttpConnector object
        ArticleHttpConnector articleHttpConnector = mock(ArticleHttpConnector.class);
        when(articleHttpConnector.createArticleConnector(anyString(), anyString())).thenReturn(httpResponse);
        // set mocked ArticleHttpConnector on ArticleApiConnection
        ArticleApiConnection articleApiConnection = new ArticleApiConnection();
        articleApiConnection.setArticleHttpConnector(articleHttpConnector);
        // assert
        Assert.assertThrows(IllegalArgumentException.class, () -> articleApiConnection.createArticle(article));
    }

    @Test
    public void createArticleConnectionErrorTest() throws IOException, URISyntaxException, InterruptedException {
        // mock ArticleHttpConnector object to throw IOException
        ArticleHttpConnector articleHttpConnector = mock(ArticleHttpConnector.class);
        when(articleHttpConnector.createArticleConnector(anyString(), anyString())).thenThrow(IOException.class);
        // set mocked ArticleHttpConnector on ArticleApiConnection
        ArticleApiConnection articleApiConnection = new ArticleApiConnection();
        articleApiConnection.setArticleHttpConnector(articleHttpConnector);
        // assert
        Assert.assertThrows(HttpException.class, () -> articleApiConnection.createArticle(article));

    }

    @Test
    public void getArticlesOfOwnerSuccessTest() throws URISyntaxException, IOException, InterruptedException, HttpException {
        String JSONList = """
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
                  "owner" : "qwerty",
                  "cost" : 2001.5,
                  "user_gets" : "user gets1!",
                  "cost_type" : "TOTAL",
                  "users_min" : 1,
                  "users_max" : 2
                }]""";

        Article article1 = new Article(
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

        Article article2 = new Article(
                null,
                "name1",
                "image1.jpg",
                "link1.com",
                "user gets1!",
                null,
                new Date(1727665200L), //2024-09-30 00:00:00
                "qwerty",
                null,
                null,
                null,
                2001.5,
                CostType.TOTAL,
                1,
                2
        );

        List<Article> articles = new ArrayList<>(List.of(article1, article2));

        // mock HttpResponse object
        HttpResponse<String> httpResponse = mock(HttpResponse.class);
        when(httpResponse.body()).thenReturn(JSONList);
        when(httpResponse.statusCode()).thenReturn(200);
        // mock ArticleHttpConnector object to throw IOException
        ArticleHttpConnector articleHttpConnector = mock(ArticleHttpConnector.class);
        when(articleHttpConnector.getArticles(anyString())).thenReturn(httpResponse);
        // set mocked ArticleHttpConnector on ArticleApiConnection
        ArticleApiConnection articleApiConnection = new ArticleApiConnection();
        articleApiConnection.setArticleHttpConnector(articleHttpConnector);
        // assert
        List<Article> gottenArticles = articleApiConnection.getArticlesOf("qwerty");

        ModelEqualsHelper.assertEquals(articles.get(0), gottenArticles.get(0));
        ModelEqualsHelper.assertEquals(articles.get(1), gottenArticles.get(1));

    }

    @Test
    public void getAllArticlesSuccessTest() throws URISyntaxException, IOException, InterruptedException, HttpException {
        String JSONList = """
                [{
                  "name" : "name",
                  "image" : "image.jpg",
                  "link" : "link.com",
                  "deadline" : "2024-05-30",
                  "owner" : "qwerty1",
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
                  "owner" : "qwerty2",
                  "cost" : 2001.5,
                  "user_gets" : "user gets1!",
                  "cost_type" : "TOTAL",
                  "users_min" : 1,
                  "users_max" : 2
                }]""";

        Article article1 = new Article(
                null,
                "name",
                "image.jpg",
                "link.com",
                "user gets!",
                null,
                new Date(1717038000000L), //2024-05-30 00:00:00
                "qwerty1",
                null,
                null,
                null,
                200.5,
                CostType.TOTAL,
                2,
                4
        );

        Article article2 = new Article(
                null,
                "name1",
                "image1.jpg",
                "link1.com",
                "user gets1!",
                null,
                new Date(1727665200L), //2024-09-30 00:00:00
                "qwerty2",
                null,
                null,
                null,
                2001.5,
                CostType.TOTAL,
                1,
                2
        );

        List<Article> articles = new ArrayList<>(List.of(article1, article2));

        // mock HttpResponse object
        HttpResponse<String> httpResponse = mock(HttpResponse.class);
        when(httpResponse.body()).thenReturn(JSONList);
        when(httpResponse.statusCode()).thenReturn(200);
        // mock ArticleHttpConnector object to throw IOException
        ArticleHttpConnector articleHttpConnector = mock(ArticleHttpConnector.class);
        when(articleHttpConnector.getArticles(null)).thenReturn(httpResponse);
        // set mocked ArticleHttpConnector on ArticleApiConnection
        ArticleApiConnection articleApiConnection = new ArticleApiConnection();
        articleApiConnection.setArticleHttpConnector(articleHttpConnector);
        // assert
        List<Article> gottenArticles = articleApiConnection.getAllArticles();

        ModelEqualsHelper.assertEquals(articles.get(0), gottenArticles.get(0));
        ModelEqualsHelper.assertEquals(articles.get(1), gottenArticles.get(1));

    }

    @Test
    public void getAllArticlesReturnsNoneSuccessTest() throws URISyntaxException, IOException, InterruptedException, HttpException {
        String JSONList = "[]";

        // mock HttpResponse object
        HttpResponse<String> httpResponse = mock(HttpResponse.class);
        when(httpResponse.body()).thenReturn(JSONList);
        when(httpResponse.statusCode()).thenReturn(200);
        // mock ArticleHttpConnector object to throw IOException
        ArticleHttpConnector articleHttpConnector = mock(ArticleHttpConnector.class);
        when(articleHttpConnector.getArticles(null)).thenReturn(httpResponse);
        // set mocked ArticleHttpConnector on ArticleApiConnection
        ArticleApiConnection articleApiConnection = new ArticleApiConnection();
        articleApiConnection.setArticleHttpConnector(articleHttpConnector);
        // assert
        List<Article> gottenArticles = articleApiConnection.getAllArticles();

        Assert.assertTrue(gottenArticles.isEmpty());

    }

    @Test
    public void getAllArticlesInputErrorTest() throws URISyntaxException, IOException, InterruptedException {
        String JSONList = "[]";

        // mock HttpResponse object
        HttpResponse<String> httpResponse = mock(HttpResponse.class);
        when(httpResponse.body()).thenReturn(JSONList);
        when(httpResponse.statusCode()).thenReturn(403);
        // mock ArticleHttpConnector object to throw IOException
        ArticleHttpConnector articleHttpConnector = mock(ArticleHttpConnector.class);
        when(articleHttpConnector.getArticles(null)).thenReturn(httpResponse);
        // set mocked ArticleHttpConnector on ArticleApiConnection
        ArticleApiConnection articleApiConnection = new ArticleApiConnection();
        articleApiConnection.setArticleHttpConnector(articleHttpConnector);
        // assert
        Assert.assertThrows(IllegalArgumentException.class, articleApiConnection::getAllArticles);

    }

    @Test
    public void getAllArticlesConnectionErrorTest() throws URISyntaxException, IOException, InterruptedException {
        String JSONList = "[]";

        // mock ArticleHttpConnector object to throw IOException
        ArticleHttpConnector articleHttpConnector = mock(ArticleHttpConnector.class);
        when(articleHttpConnector.getArticles(null)).thenThrow(IOException.class);
        // set mocked ArticleHttpConnector on ArticleApiConnection
        ArticleApiConnection articleApiConnection = new ArticleApiConnection();
        articleApiConnection.setArticleHttpConnector(articleHttpConnector);
        // assert
        Assert.assertThrows(HttpException.class, articleApiConnection::getAllArticles);

    }

    @Test
    public void subscribeToArticleSuccessTest() throws URISyntaxException, IOException, InterruptedException, HttpException {
        Article article = new Article(
                "qwerty",
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
        // mock HttpResponse object
        HttpResponse<String> httpResponse = mock(HttpResponse.class);
        when(httpResponse.statusCode()).thenReturn(200);
        // mock ArticleHttpConnector object to throw IOException
        ArticleHttpConnector articleHttpConnector = mock(ArticleHttpConnector.class);
        when(articleHttpConnector.suscribeToArticle(anyString(),anyString())).thenReturn(httpResponse);
        // set mocked ArticleHttpConnector on ArticleApiConnection
        ArticleApiConnection articleApiConnection = new ArticleApiConnection();
        articleApiConnection.setArticleHttpConnector(articleHttpConnector);
        // assert
        Assert.assertTrue(articleApiConnection.suscribeToArticle(article, "qwerty"));

    }

    @Test
    public void subscribeToArticleFailTest() throws URISyntaxException, IOException, InterruptedException, HttpException {
        Article article = new Article(
                "qwerty",
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
        // mock HttpResponse object
        HttpResponse<String> httpResponse = mock(HttpResponse.class);
        when(httpResponse.statusCode()).thenReturn(403);
        // mock ArticleHttpConnector object to throw IOException
        ArticleHttpConnector articleHttpConnector = mock(ArticleHttpConnector.class);
        when(articleHttpConnector.suscribeToArticle(anyString(),anyString())).thenReturn(httpResponse);
        // set mocked ArticleHttpConnector on ArticleApiConnection
        ArticleApiConnection articleApiConnection = new ArticleApiConnection();
        articleApiConnection.setArticleHttpConnector(articleHttpConnector);
        // assert
        Assert.assertFalse(articleApiConnection.suscribeToArticle(article, "qwerty"));

    }

    @Test
    public void subscribeToArticleConnectionErrorTest() throws URISyntaxException, IOException, InterruptedException, HttpException {
        Article article = new Article(
                "qwerty",
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
        // mock ArticleHttpConnector object to throw InterruptedException
        ArticleHttpConnector articleHttpConnector = mock(ArticleHttpConnector.class);
        when(articleHttpConnector.suscribeToArticle(anyString(),anyString())).thenThrow(InterruptedException.class);
        // set mocked ArticleHttpConnector on ArticleApiConnection
        ArticleApiConnection articleApiConnection = new ArticleApiConnection();
        articleApiConnection.setArticleHttpConnector(articleHttpConnector);
        // assert
        Assert.assertThrows(HttpException.class, () -> articleApiConnection.suscribeToArticle(article, "qwerty"));

    }

    @Test
    public void closeArticleSuccessTest() throws URISyntaxException, IOException, InterruptedException, HttpException {
        Article article = new Article(
                "qwerty",
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
        // mock HttpResponse object
        HttpResponse<String> httpResponse = mock(HttpResponse.class);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn("{\"id\":\"qwerty\",\"name\":\"name\",\"image\":\"image.jpg\",\"link\":\"link.com\",\"owner\":\"qwerty\",\"deadline\":\"2024-05-30\",\"cost\":200.5,\"user_gets\":\"user gets!\",\"cost_type\":\"TOTAL\",\"users_min\":2,\"users_max\":4}");

        // mock ArticleHttpConnector object to return mocked response
        ArticleHttpConnector articleHttpConnector = mock(ArticleHttpConnector.class);
        when(articleHttpConnector.closeArticle(anyString(), anyString())).thenReturn(httpResponse);

        // set mocked ArticleHttpConnector on ArticleApiConnection
        ArticleApiConnection articleApiConnection = new ArticleApiConnection();
        articleApiConnection.setArticleHttpConnector(articleHttpConnector);

        // assert
        Article closedArticle = articleApiConnection.closeArticle(article, "qwerty");
        Assert.assertNotNull(closedArticle);
        Assert.assertEquals(article.getId(), closedArticle.getId());
    }

    @Test
    public void closeArticleFailTest() throws URISyntaxException, IOException, InterruptedException, HttpException {
        Article article = new Article(
                "qwerty",
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
        // mock HttpResponse object
        HttpResponse<String> httpResponse = mock(HttpResponse.class);
        when(httpResponse.statusCode()).thenReturn(403);

        // mock ArticleHttpConnector object to return mocked response
        ArticleHttpConnector articleHttpConnector = mock(ArticleHttpConnector.class);
        when(articleHttpConnector.closeArticle(anyString(), anyString())).thenReturn(httpResponse);

        // set mocked ArticleHttpConnector on ArticleApiConnection
        ArticleApiConnection articleApiConnection = new ArticleApiConnection();
        articleApiConnection.setArticleHttpConnector(articleHttpConnector);

        // assert
        Assert.assertThrows(IllegalArgumentException.class, () -> articleApiConnection.closeArticle(article, "qwerty"));
    }

    @Test
    public void closeArticleConnectionErrorTest() throws URISyntaxException, IOException, InterruptedException {
        Article article = new Article(
                "qwerty",
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

        // mock ArticleHttpConnector object to throw InterruptedException
        ArticleHttpConnector articleHttpConnector = mock(ArticleHttpConnector.class);
        when(articleHttpConnector.closeArticle(anyString(), anyString())).thenThrow(InterruptedException.class);

        // set mocked ArticleHttpConnector on ArticleApiConnection
        ArticleApiConnection articleApiConnection = new ArticleApiConnection();
        articleApiConnection.setArticleHttpConnector(articleHttpConnector);

        // assert
        Assert.assertThrows(HttpException.class, () -> articleApiConnection.closeArticle(article, "qwerty"));
    }

    @Test
    public void viewArticleSubscriptionsSuccessTest() throws URISyntaxException, IOException, InterruptedException, HttpException {
        Article article = new Article(
                "qwerty",
                "name",
                "image.jpg",
                "link.com",
                "user gets!",
                null,
                new Date(2024, 5, 30), // Esto debe ser ajustado para usar el constructor de Date apropiadamente
                "qwerty",
                null,
                null,
                null,
                200.5,
                CostType.TOTAL,
                2,
                4
        );

        String JSONList = """
                [{
                  "user": {
                    "surname": "Doe",
                    "name": "John",
                    "email": "john.doe@example.com"
                  },
                  "created_at": "2024-06-21 14:30:00"
                },
                {
                  "user": {
                    "surname": "Smith",
                    "name": "Jane",
                    "email": "jane.smith@example.com"
                  },
                  "created_at": "2024-06-21 17:30:00"
                }]""";

        // mock HttpResponse object
        HttpResponse<String> httpResponse = mock(HttpResponse.class);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(JSONList);

        // mock ArticleHttpConnector object to return mocked response
        ArticleHttpConnector articleHttpConnector = mock(ArticleHttpConnector.class);
        when(articleHttpConnector.getSubscriptions(anyString())).thenReturn(httpResponse);

        // set mocked ArticleHttpConnector on ArticleApiConnection
        ArticleApiConnection articleApiConnection = new ArticleApiConnection();
        articleApiConnection.setArticleHttpConnector(articleHttpConnector);

        // assert
        List<Annotation> annotations = articleApiConnection.viewArticleSubscriptions(article);
        Assert.assertEquals(2, annotations.size());


        // Verify the contents of annotations
        Annotation annotation1 = annotations.get(0);
        Assert.assertEquals("John", annotation1.getUser().getName());
        Assert.assertEquals("Doe", annotation1.getUser().getSurname());
        Assert.assertEquals("john.doe@example.com", annotation1.getUser().getEmail());
        Assert.assertEquals("2024-06-21 14:30:00", dateFormat.format(annotation1.getDate()));

        Annotation annotation2 = annotations.get(1);
        Assert.assertEquals("Jane", annotation2.getUser().getName());
        Assert.assertEquals("Smith", annotation2.getUser().getSurname());
        Assert.assertEquals("jane.smith@example.com", annotation2.getUser().getEmail());
        Assert.assertEquals("2024-06-21 17:30:00", dateFormat.format(annotation2.getDate()));
    }

    @Test
    public void testViewArticleSubscriptions_IllegalArgumentException() throws URISyntaxException, IOException, InterruptedException {
        // Mock HttpResponse object
        HttpResponse<String> httpResponse = mock(HttpResponse.class);
        when(httpResponse.statusCode()).thenReturn(400); // Simulamos un código de estado diferente de 200
        when(httpResponse.body()).thenReturn("Error message");

        // Mock ArticleHttpConnector object to return mocked response
        ArticleHttpConnector articleHttpConnector = mock(ArticleHttpConnector.class);
        when(articleHttpConnector.getSubscriptions(anyString())).thenReturn(httpResponse);

        // set mocked ArticleHttpConnector on ArticleApiConnection
        ArticleApiConnection articleApiConnection = new ArticleApiConnection();
        articleApiConnection.setArticleHttpConnector(articleHttpConnector);

        // Create a sample article
        Article article = new Article();
        article.setId("qwerty");

        // Assert that IllegalArgumentException is thrown
        Assert.assertThrows(IllegalArgumentException.class, () -> {
            articleApiConnection.viewArticleSubscriptions(article);
        });
    }


    @Test
    public void testViewArticleSubscriptions_HttpException() throws URISyntaxException, IOException, InterruptedException {
        // Mock ArticleHttpConnector to throw IOException
        ArticleHttpConnector articleHttpConnector = mock(ArticleHttpConnector.class);
        when(articleHttpConnector.getSubscriptions(anyString())).thenThrow(IOException.class);

        // set mocked ArticleHttpConnector on ArticleApiConnection
        ArticleApiConnection articleApiConnection = new ArticleApiConnection();
        articleApiConnection.setArticleHttpConnector(articleHttpConnector);

        // Create a sample article
        Article article = new Article();
        article.setId("qwerty");

        // Assert that HttpException is thrown
        Assert.assertThrows(HttpException.class, () -> {
            articleApiConnection.viewArticleSubscriptions(article);
        });
    }

}
