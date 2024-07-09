package ar.edu.utn.frba.tests.bot;

import ar.edu.utn.frba.tests.helpers.ModelEqualsHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.tacsbot.api.notification.impl.NotificationApiConnection;
import org.tacsbot.api.notification.impl.NotificationHttpConnector;
import org.tacsbot.bot.MyTelegramBot;
import org.tacsbot.cache.impl.RedisService;
import org.tacsbot.dictionary.MessageDictionary;
import org.tacsbot.dictionary.impl.JSONMessageDictionary;
import org.tacsbot.exceptions.UnauthorizedException;
import org.tacsbot.handlers.CommandsHandler;
import org.tacsbot.model.NotificationDTO;
import org.tacsbot.model.UserSession;
import org.tacsbot.parser.article.impl.ArticleJSONParser;
import org.tacsbot.parser.user.impl.UserJSONParser;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.embedded.RedisServer;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MyTelegramBotTest {
    @InjectMocks
    private MyTelegramBot myTelegramBot;

    @Mock
    private CommandsHandler mockCommandsHandler;

    private final Long userId = 123456789L;

    private static RedisServer embeddedRedis;

    private UserSession userSession = new UserSession(
            "abcdefghi",
            "Thiago",
            "Cabrera",
            "thiago@tacs.com"
    );

    private Long chatId = 1234546789L;

    ObjectMapper objectMapper = new ObjectMapper();
    NotificationApiConnection api = new NotificationApiConnection();
    NotificationHttpConnector connector;
    static RedisService redisService;

    private void assertLogIn(Long chatId, UserSession userSession){
        Assert.assertEquals(chatId, myTelegramBot.getCacheService().getChatIdOfSession(userSession));

        Assert.assertEquals(userSession,myTelegramBot.getCacheService().getSession(chatId));
    }

    @BeforeClass
    public static void startEmbeddedRedis() throws IOException {
        embeddedRedis = new RedisServer(6379);
        embeddedRedis.start();
        JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), "redis://localhost:6379");
        redisService = new RedisService(jedisPool, new ArticleJSONParser(), new UserJSONParser(), 20L);

    }

    @AfterClass
    public static void stopEmbeddedRedisAndCloseConnections() throws IOException {
        embeddedRedis.stop();
    }
    @Before
    public void startBot(){
        myTelegramBot = spy(new MyTelegramBot(redisService));
        doNothing().when(myTelegramBot).sendText(any(),any());
        doNothing().when(myTelegramBot).sendInteraction(any(),any());
    }

    @After
    public void cleanCacheAfterEachTest(){
        myTelegramBot.logOutUser(chatId,userSession);
//        myTelegramBot.getCacheService().closeConnection();
    }

    @Test
    public void loginSavesDoubleMapping() throws IOException {

        myTelegramBot.logInUser(chatId, userSession);

        Assert.assertEquals(chatId, myTelegramBot.getCacheService().getChatIdOfSession(userSession));

        Assert.assertEquals(userSession,myTelegramBot.getCacheService().getSession(chatId));

    }

    @Test
    public void logoutDeletesDoubleMapping() throws IOException {

        myTelegramBot.logInUser(chatId, userSession);

        assertLogIn(chatId, userSession);

        myTelegramBot.logOutUser(chatId, userSession);

        Assert.assertNull(myTelegramBot.getCacheService().getChatIdOfSession(userSession));

        Assert.assertNull(myTelegramBot.getCacheService().getSession(chatId));

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
    public void initializeNotif() throws IOException, URISyntaxException, InterruptedException {
        connector = mock(NotificationHttpConnector.class);
        HttpResponse<String> response = mock(HttpResponse.class);
        List<NotificationDTO> notificationDTOList = getNotificationDTOS();
        doReturn(objectMapper.writeValueAsString(notificationDTOList)).when(response).body();
        doReturn(200).when(response).statusCode();
        doReturn(response).when(connector).getPendingNotificationsConnector();
        myTelegramBot.logInUser(chatId, userSession);
        HttpResponse<String> response2 = mock(HttpResponse.class);
        doReturn(200).when(response2).statusCode();
        doReturn(response2).when(connector).markAsNotifiedConnector(any());
        api.setNotificationHttpConnector(connector);
        myTelegramBot.setNotificationApi(api);
    }
    @Test
    public void testCheckNotificationOk() throws IOException, URISyntaxException, InterruptedException {
        initializeNotif();
        myTelegramBot.checkPendingNotifications();
        verify(connector).getPendingNotificationsConnector();
        verify(connector).markAsNotifiedConnector(eq("id1"));
        verify(connector).markAsNotifiedConnector(eq("id2"));
        verify(connector).markAsNotifiedConnector(eq("id3"));
        verify(connector).markAsNotifiedConnector(eq("id4"));
    }
    @Test
    public void testCheckNotificationNoNotif() throws IOException, URISyntaxException, InterruptedException {
        initializeNotif();
        HttpResponse<String> response = mock(HttpResponse.class);
        doReturn(400).when(response).statusCode();
        doReturn(objectMapper.writeValueAsString(new ArrayList<NotificationDTO>())).when(response).body();
        doReturn(response).when(connector).getPendingNotificationsConnector();
        myTelegramBot.checkPendingNotifications();
        verify(connector).getPendingNotificationsConnector();
        verify(connector,never()).markAsNotifiedConnector(any());
    }
    @Test
    public void testCheckNotificationErrorFetch() throws IOException, URISyntaxException, InterruptedException {
        initializeNotif();
        HttpResponse<String> response = mock(HttpResponse.class);
        doReturn(400).when(response).statusCode();
        doReturn(response).when(connector).getPendingNotificationsConnector();
        myTelegramBot.checkPendingNotifications();
        verify(connector).getPendingNotificationsConnector();
        verify(connector,never()).markAsNotifiedConnector(any());
    }
    @Test
    public void testCheckNotificationErrorNoChat() throws IOException, URISyntaxException, InterruptedException {
        initializeNotif();
        List<NotificationDTO> notificationDTOList = new ArrayList<>();
        NotificationDTO notificationDTOSubscribe = new NotificationDTO("id1", "SubscriptionNotification", "article", "abc", false, new Date(), 1, 1, 4);
        notificationDTOList.add(notificationDTOSubscribe);
        HttpResponse<String> response = mock(HttpResponse.class);
        doReturn(200).when(response).statusCode();
        doReturn(objectMapper.writeValueAsString(notificationDTOList)).when(response).body();
        doReturn(response).when(connector).getPendingNotificationsConnector();
        myTelegramBot.checkPendingNotifications();
        verify(connector).getPendingNotificationsConnector();
        verify(connector,never()).markAsNotifiedConnector(any());
    }
    @Test
    public void testCheckNotificationErrorMark() throws IOException, URISyntaxException, InterruptedException {
        initializeNotif();
        HttpResponse<String> response = mock(HttpResponse.class);
        doReturn(400).when(response).statusCode();
        doReturn(response).when(connector).markAsNotifiedConnector(any());
        myTelegramBot.checkPendingNotifications();
        verify(connector).getPendingNotificationsConnector();
    }
    @Test
    public void testCheckNotificationErrorMarkException() throws IOException, URISyntaxException, InterruptedException {
        initializeNotif();
        doThrow(URISyntaxException.class).when(connector).markAsNotifiedConnector(any());
        myTelegramBot.checkPendingNotifications();
        verify(connector).getPendingNotificationsConnector();
    }

    private static List<NotificationDTO> getNotificationDTOS() {
        List<NotificationDTO> notificationDTOList = new ArrayList<>();
        NotificationDTO notificationDTOSubscribe = new NotificationDTO("id1", "SubscriptionNotification", "article", "abcdefghi", false, new Date(), 1, 1, 4);
        NotificationDTO notificationDTOClose = new NotificationDTO("id2", "ClosedArticleNotification", "article", "abcdefghi", false, new Date(), 1, 1, 4);
        NotificationDTO notificationDTOOwnerSubscribe = new NotificationDTO("id3", "SubscriptionNotification", "article", "abcdefghi", false, new Date(), 1, 1, 4);
        NotificationDTO notificationDTOOwnerClose = new NotificationDTO("id4", "ClosedArticleNotification", "article", "abcdefghi", false, new Date(), 1, 1, 4);
        notificationDTOList.add(notificationDTOSubscribe);
        notificationDTOList.add(notificationDTOOwnerSubscribe);
        notificationDTOList.add(notificationDTOClose);
        notificationDTOList.add(notificationDTOOwnerClose);
        return notificationDTOList;
    }
    @Test
    public void testCreateArticle() throws IOException {
        myTelegramBot.logInUser(chatId, userSession);
        Message message = new Message();
        message.setText("/crear_articulo");
        message.setChat(new Chat(chatId,"type"));
        org.telegram.telegrambots.meta.api.objects.User telegramuser = new org.telegram.telegrambots.meta.api.objects.User();
        telegramuser.setId(123L);
        telegramuser.setFirstName("name");
        message.setFrom(telegramuser);
        myTelegramBot.createArticle(message,message.getText());
        verify(myTelegramBot).sendInteraction(any(),eq("ARTICLE_NAME"));
    }
    @Test
    public void testSearchArticlesLogged() throws IOException, UnauthorizedException {
        myTelegramBot.logInUser(chatId, userSession);
        Message message = new Message();
        message.setText("/obtener_articulos");
        message.setChat(new Chat(chatId,"type"));
        org.telegram.telegrambots.meta.api.objects.User telegramuser = new org.telegram.telegrambots.meta.api.objects.User();
        telegramuser.setId(123L);
        telegramuser.setFirstName("name");
        message.setFrom(telegramuser);
        myTelegramBot.searchArticles(message,message.getText());
        verify(myTelegramBot).sendInteraction(any(),eq("CHOOSE_ARTICLE_SEARCH"));
    }
    @Test
    public void testRegister() throws IOException {
        Message message = new Message();
        message.setText("/obtener_articulos");
        message.setChat(new Chat(chatId,"type"));
        org.telegram.telegrambots.meta.api.objects.User telegramuser = new org.telegram.telegrambots.meta.api.objects.User();
        telegramuser.setId(123L);
        telegramuser.setFirstName("name");
        message.setFrom(telegramuser);
        myTelegramBot.register(message,message.getText());
        verify(myTelegramBot).sendInteraction(any(),eq("REGISTER_NAME"));
    }
    @Test
    public void testLogin() throws IOException {
        Message message = new Message();
        message.setText("/obtener_articulos");
        message.setChat(new Chat(chatId,"type"));
        org.telegram.telegrambots.meta.api.objects.User telegramuser = new org.telegram.telegrambots.meta.api.objects.User();
        telegramuser.setId(123L);
        telegramuser.setFirstName("name");
        message.setFrom(telegramuser);
        myTelegramBot.login(message,message.getText());
        verify(myTelegramBot).sendInteraction(any(),eq("LOGIN_EMAIL"));
    }
    @Test
    public void testLogout() throws IOException {
        myTelegramBot.logInUser(chatId, userSession);
        Message message = new Message();
        message.setText("/obtener_articulos");
        message.setChat(new Chat(chatId,"type"));
        org.telegram.telegrambots.meta.api.objects.User telegramuser = new org.telegram.telegrambots.meta.api.objects.User();
        telegramuser.setId(123L);
        telegramuser.setFirstName("name");
        message.setFrom(telegramuser);
        myTelegramBot.logout(message,message.getText());
        verify(myTelegramBot).sendInteraction(any(),eq("LOG_OUT"));
    }

}
