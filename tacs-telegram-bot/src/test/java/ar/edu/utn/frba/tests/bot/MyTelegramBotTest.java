package ar.edu.utn.frba.tests.bot;

import ar.edu.utn.frba.tests.helpers.ModelEqualsHelper;
import org.junit.*;
import org.tacsbot.bot.MyTelegramBot;
import org.tacsbot.cache.impl.RedisService;
import org.tacsbot.model.User;
import org.tacsbot.parser.article.impl.ArticleJSONParser;
import org.tacsbot.parser.user.impl.UserJSONParser;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.embedded.RedisServer;

import java.io.IOException;

public class MyTelegramBotTest {

    private static MyTelegramBot myTelegramBot;

    private static RedisServer embeddedRedis;

    private User user = new User(
            "abcdefghi",
            "Thiago",
            "Cabrera",
            "thiago@tacs.com",
            null
    );

    private Long chatId = 1234546789L;

    private void assertLogIn(Long chatId, User user){
        Assert.assertEquals(chatId, myTelegramBot.getCacheService().getChatIdOfUser(user.getId()));

        ModelEqualsHelper.assertEquals(user,myTelegramBot.getCacheService().getUser(chatId));
    }

    @BeforeClass
    public static void startEmbeddedRedis() throws IOException {
        embeddedRedis = new RedisServer(6379);
        embeddedRedis.start();
        JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), "redis://localhost:6379");
        RedisService redisService = new RedisService(jedisPool, new ArticleJSONParser(), new UserJSONParser(), 20L);
        myTelegramBot = new MyTelegramBot(redisService);
    }

    @AfterClass
    public static void stopEmbeddedRedisAndCloseConnections() throws IOException {
        myTelegramBot.getCacheService().closeConnection();
        embeddedRedis.stop();
    }

    @After
    public void cleanCacheAfterEachTest(){

    }

    @Test
    public void loginSavesDoubleMapping() throws IOException {

        myTelegramBot.logInUser(chatId, user);

        Assert.assertEquals(chatId, myTelegramBot.getCacheService().getChatIdOfUser(user.getId()));

        ModelEqualsHelper.assertEquals(user,myTelegramBot.getCacheService().getUser(chatId));

    }

    @Test
    public void logoutDeletesDoubleMapping() throws IOException {

        myTelegramBot.logInUser(chatId, user);

        assertLogIn(chatId, user);

        myTelegramBot.logOutUser(chatId, user);

        Assert.assertNull(myTelegramBot.getCacheService().getChatIdOfUser(user.getId()));

        Assert.assertNull(myTelegramBot.getCacheService().getUser(chatId));

    }

}
