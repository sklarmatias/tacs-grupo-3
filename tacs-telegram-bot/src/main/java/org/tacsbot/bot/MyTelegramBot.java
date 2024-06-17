package org.tacsbot.bot;
import lombok.Setter;
import org.apache.http.HttpException;
import org.tacsbot.api.notification.NotificationApi;
import org.tacsbot.api.notification.impl.NotificationApiConnection;
import org.tacsbot.dictionary.JSONMessageDictionary;
import org.tacsbot.dictionary.MessageDictionary;
import org.tacsbot.handlers.impl.*;
import org.tacsbot.handlers.*;
import org.tacsbot.model.NotificationDTO;
import org.tacsbot.model.UserChatMapping;
import org.tacsbot.model.Annotation;
import org.tacsbot.model.Article;
import org.tacsbot.model.User;
import org.tacsbot.redis.RedisService;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MyTelegramBot extends TelegramLongPollingBot {

    @Setter
    private MessageDictionary messageDictionary;

    private final Map<String, CommandAction> commandActions = new HashMap<>();

    public final Map<Long, CommandsHandler> commandsHandlerMap = new HashMap<>();
    public final UserChatMapping usersLoginMap = new UserChatMapping();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final NotificationApi notificationApi = new NotificationApiConnection();

    public final Map<Long, User> loggedUsersMap = new HashMap<>();

    private RedisService redisService;

    public MyTelegramBot() {
        super(System.getenv("BOT_TOKEN"));
        // Initialization of the commandActions map with the actions associated with the commands
        commandActions.put("/help", this::helpCommand);
        commandActions.put("/crear_articulo", this::createArticle);
        commandActions.put("/obtener_articulos", this::searchArticles);
        commandActions.put("/login", this::login);
        commandActions.put("/logout", this::logout);
        commandActions.put("/registrarme", this::register);

        messageDictionary = new JSONMessageDictionary();

        redisService = new RedisService();

    }

    @Override
    public String getBotUsername() {
        return System.getenv("BOT_USERNAME");
    }

    public void sendInternalErrorMsg(org.telegram.telegrambots.meta.api.objects.User user, Exception exception){
        sendInteraction(user, "INTERNAL_ERROR");
        System.out.printf("[Error] Error:\n%s\n", exception.getMessage());
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message msg = update.getMessage();
        org.telegram.telegrambots.meta.api.objects.User user = msg.getFrom();
        Long id = user.getId();
        String txt = msg.getText();
        System.out.println(user.getFirstName() + " wrote " + msg.getText() + " from " + user.getId());

        if(msg.isCommand()){
            resetUserHandlers(id);
            String command = msg.getText();

            if (commandActions.containsKey(command)){
                try{
                    commandActions.get(command).execute(msg, txt);
                } catch (Exception e){
                    sendInternalErrorMsg(user, e);
                }
            } else{
                sendInteraction(user, "UNKNOWN_COMMAND");
            }

        }else if (commandsHandlerMap.containsKey(id)){
            try {
                commandsHandlerMap.get(id).processResponse(msg, this);
            } catch (Exception e){
                sendInternalErrorMsg(user, e);
            }

        }else sendInteraction(user, "WELCOME");
    }

    // commands

    private void helpCommand(Message message, String commandText) {

        sendInteraction(message.getFrom(), "HELP");

    }

    private void createArticle(Message message, String commandText) {

        if(usersLoginMap.containsChatIdKey(message.getChatId())) {
            System.out.println("User is logged in");
            commandsHandlerMap.remove(message.getFrom().getId());
            ArticleCreationHandler handler = new ArticleCreationHandler(message.getFrom().getId());
            commandsHandlerMap.put(message.getFrom().getId(), handler);
            sendInteraction(message.getFrom(), "ARTICLE_NAME");
        }
        else{
            sendInteraction(message.getFrom(), "LOGIN_REQUIRED");
        }

    }


    private void searchArticles(Message message, String commandText) {
        Long chatId = message.getChatId();
        if(usersLoginMap.containsChatIdKey(chatId)) {
            commandsHandlerMap.remove(chatId);
            ArticleHandler handler = new ArticleHandler(chatId);
            commandsHandlerMap.put(chatId, handler);
            sendInteraction(message.getFrom(), "CHOOSE_ARTICLE_SEARCH");
        }
        else{
            ArticleHandler handler = new ArticleHandler(chatId);
            handler.setArticleType(ArticleType.TODOS);
            try{
                handler.processResponse(message, this);
            } catch(HttpException e){
                sendInternalErrorMsg(message.getFrom(), e);
            }

        }
    }

    private void register(Message message, String commandText) {
        Long chatId = message.getChatId();
        if(usersLoginMap.containsChatIdKey(message.getChatId())){
            User u = loggedUsersMap.get(chatId);
            sendInteraction(message.getFrom(), "ALREADY_LOGGED_IN", u.getName());
        }
        else{
            commandsHandlerMap.remove(chatId);
            RegisterHandler handler = new RegisterHandler(chatId);
            commandsHandlerMap.put(chatId, handler);
            sendInteraction(message.getFrom(), "REGISTER_NAME");
        }


    }
    private void login(Message message, String commandText){
        Long chatId = message.getChatId();
        if(usersLoginMap.containsChatIdKey(chatId)){
            User u = loggedUsersMap.get(chatId);
            sendInteraction(message.getFrom(), "ALREADY_LOGGED_IN", u.getName());
        }
        else{
            commandsHandlerMap.remove(chatId);
            LoginHandler handler = new LoginHandler(chatId);
            commandsHandlerMap.put(chatId, handler);
            sendInteraction(message.getFrom(), "LOGIN_EMAIL");
        }
    }
    private void logout(Message message, String commandText){
        Long chatId = message.getChatId();
        if(usersLoginMap.containsChatIdKey(chatId)){
            usersLoginMap.removeByChatId(chatId);
            sendInteraction(message.getFrom(), "LOG_OUT");

        }
        else{
            sendInteraction(message.getFrom(), "LOGIN_REQUIRED");
        }

    }

    // commons

    public void sendArticle(org.telegram.telegrambots.meta.api.objects.User telegramUser, Article article){
        sendText(telegramUser.getId(), messageDictionary.articleToString(article, telegramUser.getLanguageCode()));
    }

    public void sendAnnotationList(org.telegram.telegrambots.meta.api.objects.User telegramUser, List<Annotation> annotationList){
        sendText(telegramUser.getId(),
                messageDictionary.annotationListToString(annotationList, telegramUser.getLanguageCode()));
    }

    public void sendArticleList(org.telegram.telegrambots.meta.api.objects.User telegramUser, List<Article> articleList){
        sendText(telegramUser.getId(),
                messageDictionary.articleListToString(articleList, telegramUser.getLanguageCode()));
    }

    private String getTranslatedMessage(org.telegram.telegrambots.meta.api.objects.User telegramUser, String interaction){
        return messageDictionary.getMessage(interaction, telegramUser.getLanguageCode());
    }

    public void sendInteraction(org.telegram.telegrambots.meta.api.objects.User telegramUser, String interaction, Object... objects){
        sendText(telegramUser.getId(),
                String.format(getTranslatedMessage(telegramUser, interaction), objects));
    }

    public void sendText(Long who, String what, boolean enableMarkup){
        SendMessage sm = SendMessage.builder()
                .chatId(who.toString()) //Who are we sending a message to
                .text(what).build();    //Message content
        sm.enableMarkdown(enableMarkup);
        try {
            this.execute(sm);                        //Actually sending the message
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);      //Any error will be printed here
        }
    }

    public void sendText(Long who, String what){
        sendText(who, what, false);
    }

    public void scheduleNotificationChecks() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::checkPendingNotifications, 0, Long.parseLong(System.getenv("NOTIFICATION_REFRESHING_TIME")), TimeUnit.SECONDS);
    }

    public void checkPendingNotifications() {
        try {
            System.out.println("Intentando enviar notificaciones...");
            List<NotificationDTO> notifications = notificationApi.getPendingNotifications();
            for (NotificationDTO notification : notifications) {
                Long chatId = usersLoginMap.getChatId(notification.getSubscriber());
                if (chatId == null) {
                    System.out.println("Chat ID is null for user: " + notification.getSubscriber());
                    continue;  // Skip this notification
                }
                try {
                    String message = generateMessage(notification);
                    sendText(chatId, message);
                    boolean marked = notificationApi.markAsNotified(notification.getId());
                    if (!marked) {
                        System.err.println("Failed to mark notification as notified: " + notification.getId());
                    }
                } catch (Exception e) {
                    System.err.println("Error sending notification: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching pending notifications: " + e.getMessage());
        }
    }

    private String generateMessage(NotificationDTO notification) {
        return switch (notification.getType()) {
            case "ClosedArticleNotification" ->
                    "El artículo \"" + notification.getArticleName() + "\" ha sido cerrado.";
            case "OwnerClosedArticleNotification" ->
                    "Tu artículo \"" + notification.getArticleName() + "\" ha sido cerrado.";
            case "SubscriptionNotification" -> "Te has suscrito al artículo \"" + notification.getArticleName() + "\".";
            case "OwnerSubscriptionNotification" ->
                    "Alguien se ha suscrito a tu artículo \"" + notification.getArticleName() + "\".";
            default -> "Tienes una nueva notificación sobre el artículo \"" + notification.getArticleName() + "\".";
        };
    }


    public void resetUserHandlers(Long userId){
        commandsHandlerMap.remove(userId);
    }


    public void loginUser(Long chatId, User savedUser) throws IOException {

        redisService.saveUser(chatId.toString(), savedUser);

    }
}