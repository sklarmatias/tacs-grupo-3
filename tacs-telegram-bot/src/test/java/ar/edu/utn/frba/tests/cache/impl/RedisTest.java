package ar.edu.utn.frba.tests.cache.impl;

import ar.edu.utn.frba.tests.helpers.ModelEqualsHelper;
import org.junit.*;
import org.tacsbot.handlers.impl.ArticleCreationHandler;
import org.tacsbot.handlers.impl.ArticleCreationStep;
import org.tacsbot.model.Article;
import org.tacsbot.model.CostType;
import org.tacsbot.model.UserSession;
import org.tacsbot.parser.article.impl.ArticleJSONParser;
import org.tacsbot.parser.user.impl.UserJSONParser;
import org.tacsbot.cache.impl.RedisService;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.embedded.RedisServer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RedisTest {

    private static RedisService redisService;

    private static RedisServer redisServer;

    private static JedisPool jedisPool;

    private ArticleJSONParser articleJSONParser = new ArticleJSONParser();

    private Long expirationSeconds = 5L;

    private Article article1 = new Article(
            "abcdefg",
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
            "hijklmno",
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

    private Long chatId = 123456789L;

    UserSession userSession = new UserSession("abcdefg", "BOT","Thiago", "Cabrera", "thiago@tacs.com");


    @BeforeClass
    public static void instanciateAll() throws IOException {

        redisServer = new RedisServer(6379);
        redisServer.start();

        jedisPool = new JedisPool(new JedisPoolConfig(), "redis://localhost:6379");
        redisService = new RedisService(jedisPool, new ArticleJSONParser(), new UserJSONParser(), 20L);
    }

    @After
    public void cleanDatabase(){
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.flushAll();
        }
    }

    @AfterClass
    public static void closeDatabase() throws IOException {
        jedisPool.close();
        redisService.closeConnection();
        redisServer.stop();
    }

    private void assertSaving() throws IOException {
        try (Jedis jedis = jedisPool.getResource()) {
            List<String> savedArticles = jedis.zrange(String.valueOf(chatId), 0 , -1);
            Assert.assertEquals(2, savedArticles.size());
            Assert.assertEquals(articleJSONParser.parseArticleToJSON(article1), savedArticles.get(0));
            Assert.assertEquals(articleJSONParser.parseArticleToJSON(article2), savedArticles.get(1));
        }
    }

    @Test
    public void saveArticleList() throws IOException {
        redisService.saveArticles(chatId, new ArrayList<>(List.of(article1, article2)));
        assertSaving();
    }

    @Test
    public void saveAndDeleteArticleList() throws IOException {
        redisService.saveArticles(chatId, new ArrayList<>(List.of(article1, article2)));
        assertSaving();
        redisService.deleteChatId(chatId);
        try (Jedis jedis = jedisPool.getResource()) {
            List<String> savedArticles = jedis.zrange(String.valueOf(chatId), 0, -1);
            Assert.assertEquals(0, savedArticles.size());
        }
    }

    @Test
    public void saveAndGetArticleList() throws IOException {
        redisService.saveArticles(chatId, new ArrayList<>(List.of(article1, article2)));
        assertSaving();
        List<Article> articles = redisService.getArticles(chatId);
        Assert.assertEquals(2, articles.size());
        ModelEqualsHelper.assertEquals(article1,articles.get(0));
        ModelEqualsHelper.assertEquals(article2,articles.get(1));
    }

    @Test
    public void expiratedArticle() throws IOException, InterruptedException {
        redisService.setExpirationSeconds(expirationSeconds);
        redisService.saveArticles(chatId, new ArrayList<>(List.of(article1, article2)));
        assertSaving();
        Thread.sleep(expirationSeconds*1000);
        try (Jedis jedis = jedisPool.getResource()) {
            List<String> strings = jedis.zrange(String.valueOf(chatId), 0, -1);
            Assert.assertEquals(0, strings.size());
        }
    }

    @Test
    public void saveCreationStep() {
        ArticleCreationHandler articleCreationHandler = new ArticleCreationHandler(userSession, article1, ArticleCreationStep.REQUEST_DEADLINE);
        redisService.saveArticleCreationHandler(chatId, articleCreationHandler);
        try (Jedis jedis = jedisPool.getResource()) {
            List<String> strings = jedis.zrange(String.valueOf(chatId), 0, -1);
            Assert.assertEquals(articleCreationHandler.getCurrentStep(), ArticleCreationStep.valueOf(strings.get(0)));
            ModelEqualsHelper.assertEquals(articleCreationHandler.getArticle(), articleJSONParser.parseJSONToArticle(strings.get(1)));
        }
    }

    @Test
    public void getCreationStep() {
        ArticleCreationHandler articleCreationHandler = new ArticleCreationHandler(userSession, article1, ArticleCreationStep.REQUEST_MAX_USERS);
        redisService.saveArticleCreationHandler(chatId, articleCreationHandler);
        ArticleCreationHandler savedArticleCreationHandler = redisService.getArticleCreationHandler(chatId);
        Assert.assertEquals(articleCreationHandler.getCurrentStep(), savedArticleCreationHandler.getCurrentStep());
        ModelEqualsHelper.assertEquals(articleCreationHandler.getArticle(), savedArticleCreationHandler.getArticle());
    }

    @Test
    public void saveAndGetUser() throws IOException {
        redisService.saveSession(chatId, userSession);
        UserSession savedSession = redisService.getSession(chatId);
        Assert.assertEquals(userSession, savedSession);
    }

    @Test
    public void getUnknownUser() {
        Assert.assertNull(redisService.getSession(12345678L));
    }

    @Test
    public void saveAndGetChatIdOfSession() {
        redisService.saveChatIdOfSession(userSession, chatId);
        Assert.assertEquals(chatId, redisService.getChatIdOfSession(userSession));
}


    @Test
    public void saveAndDeleteSession() throws IOException {
        redisService.saveSession(chatId, userSession);
        Assert.assertEquals(1, redisService.deleteSession(chatId));
    }

}
