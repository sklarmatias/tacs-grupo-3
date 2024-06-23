package ar.edu.utn.frba.tests.bot;

import ar.edu.utn.frba.tests.helpers.ModelEqualsHelper;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.tacsbot.bot.MyTelegramBot;
import org.tacsbot.cache.impl.RedisService;
import org.tacsbot.dictionary.MessageDictionary;
import org.tacsbot.dictionary.impl.JSONMessageDictionary;
import org.tacsbot.handlers.CommandsHandler;
import org.tacsbot.model.NotificationDTO;
import org.tacsbot.model.User;
import org.tacsbot.parser.article.impl.ArticleJSONParser;
import org.tacsbot.parser.user.impl.UserJSONParser;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.embedded.RedisServer;

import java.io.IOException;
import java.util.HashMap;
@RunWith(MockitoJUnitRunner.class)
public class MyTelegramBotTest {
    @InjectMocks
    private static MyTelegramBot myTelegramBot;

    @Mock
    private CommandsHandler mockCommandsHandler;

    private final Long userId = 123456789L;

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
    @Test
    public void setMessageDictionary() {
        // Crear un nuevo diccionario de mensajes para la prueba
        MessageDictionary newDictionary = new JSONMessageDictionary();

        // Establecer el nuevo diccionario
        myTelegramBot.setMessageDictionary(newDictionary);

        // Verificar que el diccionario se haya establecido correctamente
        Assert.assertEquals(newDictionary, myTelegramBot.getMessageDictionary());
    }

    @Test
    public void testConstructorWithoutArguments() {
        // Verificar que el cacheService sea un RedisService
        Assert.assertTrue(myTelegramBot.getCacheService() instanceof RedisService);
    }

    @Test
    public void testGenerateMessage() {
        // Caso 1: ClosedArticleNotification
        NotificationDTO closedArticleNotification = new NotificationDTO();
        closedArticleNotification.setType("ClosedArticleNotification");
        closedArticleNotification.setArticleName("Artículo 1");
        closedArticleNotification.setCurrentSubscribers(8);
        closedArticleNotification.setMinSubscribers(10);
        closedArticleNotification.setMaxSubscribers(20);

        String generatedMessage1 = myTelegramBot.generateMessage(closedArticleNotification);
        Assert.assertTrue(generatedMessage1.contains("🔒 El artículo \"Artículo 1\" ha sido cerrado."));
        Assert.assertTrue(generatedMessage1.contains("Suscriptores actuales: 8"));
        Assert.assertTrue(generatedMessage1.contains("Cantidad mínima de suscriptores: 10"));
        Assert.assertTrue(generatedMessage1.contains("Cantidad máxima de suscriptores: 20"));
        Assert.assertTrue(generatedMessage1.contains("❗ No se ha llegado a la cantidad mínima de suscriptores"));

        closedArticleNotification.setCurrentSubscribers(12);
        String generatedMessage2 = myTelegramBot.generateMessage(closedArticleNotification);
        Assert.assertTrue(generatedMessage2.contains("✅ Ya se puede realizar la operacion"));

        // Caso 2: OwnerClosedArticleNotification
        NotificationDTO ownerClosedArticleNotification = new NotificationDTO();
        ownerClosedArticleNotification.setType("OwnerClosedArticleNotification");
        ownerClosedArticleNotification.setArticleName("Artículo 2");
        ownerClosedArticleNotification.setCurrentSubscribers(7);
        ownerClosedArticleNotification.setMinSubscribers(10);

        String generatedMessage3 = myTelegramBot.generateMessage(ownerClosedArticleNotification);
        Assert.assertTrue(generatedMessage3.contains("🔒 Tu artículo \"Artículo 2\" ha sido cerrado."));
        Assert.assertTrue(generatedMessage3.contains("❗ No se ha llegado a la cantidad mínima de suscriptores"));

        ownerClosedArticleNotification.setCurrentSubscribers(15);
        String generatedMessage4 = myTelegramBot.generateMessage(ownerClosedArticleNotification);
        Assert.assertTrue(generatedMessage4.contains("✅ Ya se puede realizar la operacion"));

        // Caso 3: SubscriptionNotification
        NotificationDTO subscriptionNotification = new NotificationDTO();
        subscriptionNotification.setType("SubscriptionNotification");
        subscriptionNotification.setArticleName("Artículo 3");
        subscriptionNotification.setCurrentSubscribers(5);
        subscriptionNotification.setMinSubscribers(10);

        String generatedMessage5 = myTelegramBot.generateMessage(subscriptionNotification);
        Assert.assertTrue(generatedMessage5.contains("✅ Un nuevo usuario se ha suscripto al artículo \"Artículo 3\"."));
        Assert.assertTrue(generatedMessage5.contains("❗ Falta/n suscribirse 5 usuario/s como mínimo"));

        subscriptionNotification.setCurrentSubscribers(12);
        String generatedMessage6 = myTelegramBot.generateMessage(subscriptionNotification);
        Assert.assertTrue(generatedMessage6.contains("Suscriptores actuales: 12"));
        Assert.assertFalse(generatedMessage6.contains("❗")); // No debe haber advertencia si se alcanza el mínimo

        // Caso 4: OwnerSubscriptionNotification
        NotificationDTO ownerSubscriptionNotification = new NotificationDTO();
        ownerSubscriptionNotification.setType("OwnerSubscriptionNotification");
        ownerSubscriptionNotification.setArticleName("Artículo 4");
        ownerSubscriptionNotification.setCurrentSubscribers(3);
        ownerSubscriptionNotification.setMinSubscribers(5);

        String generatedMessage7 = myTelegramBot.generateMessage(ownerSubscriptionNotification);
        Assert.assertTrue(generatedMessage7.contains("✅ Un usuario se ha suscripto a tu artículo \"Artículo 4\"."));
        Assert.assertTrue(generatedMessage7.contains("❗ Falta/n suscribirse 2 usuario/s como mínimo"));

        ownerSubscriptionNotification.setCurrentSubscribers(6);
        String generatedMessage8 = myTelegramBot.generateMessage(ownerSubscriptionNotification);
        Assert.assertTrue(generatedMessage8.contains("Suscriptores actuales: 6"));
        Assert.assertFalse(generatedMessage8.contains("❗")); // No debe haber advertencia si se alcanza el mínimo

        // Caso por defecto (otro tipo de notificación)
        NotificationDTO defaultNotification = new NotificationDTO();
        defaultNotification.setType("SomeOtherNotification");
        defaultNotification.setArticleName("Artículo 5");

        String generatedMessage9 = myTelegramBot.generateMessage(defaultNotification);
        Assert.assertTrue(generatedMessage9.contains("📬 Has recibido una nueva notificación en tu publicación: Artículo 5"));
    }
    @Test
    public void testResetUserHandlers() {
        // Add a mock handler for the user ID
        myTelegramBot.commandsHandlerMap.put(userId, mockCommandsHandler);

        // Call the method under test
        myTelegramBot.resetUserHandlers(userId);

        // Verify that the handler for the user ID is removed
        Assert.assertNull(myTelegramBot.commandsHandlerMap.get(userId));
        Assert.assertEquals(0, myTelegramBot.commandsHandlerMap.size()); // Ensure the map is empty

        // Optionally, verify that any necessary cleanup or other logic is executed
        // verify(mockCommandsHandler, times(1)).cleanup(); // Example if handler has cleanup logic
    }
}
