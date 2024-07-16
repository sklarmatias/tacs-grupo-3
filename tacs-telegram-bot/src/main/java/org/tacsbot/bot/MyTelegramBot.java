package org.tacsbot.bot;
import lombok.Getter;
import lombok.Setter;
import org.apache.http.HttpException;
import org.tacsbot.api.notification.NotificationApi;
import org.tacsbot.api.notification.impl.NotificationApiConnection;
import org.tacsbot.api.report.impl.ReportApiConnection;
import org.tacsbot.dictionary.impl.JSONMessageDictionary;
import org.tacsbot.dictionary.MessageDictionary;
import org.tacsbot.exceptions.UnauthorizedException;
import org.tacsbot.handlers.impl.*;
import org.tacsbot.handlers.*;
import org.tacsbot.model.*;
import org.tacsbot.cache.impl.RedisService;
import org.tacsbot.cache.CacheService;
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
    @Getter
    private MessageDictionary messageDictionary;

    private final Map<String, CommandAction> commandActions = new HashMap<>();

    public final Map<Long, CommandsHandler> commandsHandlerMap = new HashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    @Setter
    private NotificationApi notificationApi = new NotificationApiConnection();

    @Getter
    @Setter
    private CacheService cacheService;

    public MyTelegramBot(CacheService cacheService) {
        super(System.getenv("BOT_TOKEN"));
        // Initialization of the commandActions map with the actions associated with the commands
        commandActions.put("/help", this::helpCommand);
        commandActions.put("/crear_articulo", this::createArticle);
        commandActions.put("/obtener_articulos", this::searchArticles);
        commandActions.put("/login", this::login);
        commandActions.put("/logout", this::logout);
        commandActions.put("/registrarme", this::register);
        commandActions.put("/reportes", this::reportCommand);
        messageDictionary = new JSONMessageDictionary();

        this.cacheService = cacheService;

    }

    public MyTelegramBot() {
        this(new RedisService());
    }

    @Override
    public String getBotUsername() {
        return System.getenv("BOT_USERNAME");
    }

    public void sendInternalErrorMsg(org.telegram.telegrambots.meta.api.objects.User user, Exception exception) {
        sendInteraction(user, "INTERNAL_ERROR");
        System.err.printf("[Error] Error:\n%s\n", exception.getMessage());
        exception.printStackTrace();
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message msg = update.getMessage();
        org.telegram.telegrambots.meta.api.objects.User user = msg.getFrom();
        Long id = user.getId();
        String txt = msg.getText();
        System.out.println(user.getFirstName() + " wrote " + msg.getText() + " from " + user.getId());
        if (msg.isCommand()) {
            resetUserHandlers(id);
            String command = msg.getText();

            if (commandActions.containsKey(command)) {
                try {
                    commandActions.get(command).execute(msg, txt);
                } catch (UnauthorizedException e){
                    sendInteraction(msg.getFrom(), "SESSION_CLOSED");
                    getCacheService().deleteSessionMapping(id, new UserSession(e.getSessionId()));
                }
                catch (Exception e) {
                    sendInternalErrorMsg(user, e);
                }
            } else {
                sendInteraction(user, "UNKNOWN_COMMAND");
            }

        } else if (commandsHandlerMap.containsKey(id)) {
            try {
                commandsHandlerMap.get(id).processResponse(msg, this);
            } catch (UnauthorizedException e){
                sendInteraction(msg.getFrom(), "SESSION_CLOSED");
                getCacheService().deleteSessionMapping(id, new UserSession(e.getSessionId()));
            } catch (Exception e) {
                sendInternalErrorMsg(user, e);
            }

        } else sendInteraction(user, "WELCOME");
    }

    private void addNewCommandHandler(Long chatId, CommandsHandler commandsHandler){
        commandsHandlerMap.remove(chatId);
        commandsHandlerMap.put(chatId, commandsHandler);
    }

    // commands

    private void reportCommand(Message message, String commandText){
        Long chatId = message.getChatId();

        addNewCommandHandler(chatId, new ReportHandler(new ReportApiConnection()));

        sendInteraction(message.getFrom(), "SELECT_REPORT");

    }

    private void helpCommand(Message message, String commandText) {

        sendInteraction(message.getFrom(), "HELP");

    }

    public void createArticle(Message message, String commandText) {
        Long chatId = message.getChatId();
        UserSession userSession = cacheService.getSession(chatId);
        if(userSession != null) {
            commandsHandlerMap.remove(chatId);
            ArticleCreationHandler handler = new ArticleCreationHandler(userSession);
            commandsHandlerMap.put(chatId, handler);
            sendInteraction(message.getFrom(), "ARTICLE_NAME");
        }
        else{
            sendInteraction(message.getFrom(), "LOGIN_REQUIRED");
        }

    }

    public void searchArticles(Message message, String commandText) throws UnauthorizedException {
        Long chatId = message.getChatId();
        UserSession userSession = cacheService.getSession(chatId);
        if(userSession != null) {
            commandsHandlerMap.remove(chatId);
            ArticleHandler handler = new ArticleHandler(userSession);
            commandsHandlerMap.put(chatId, handler);
            sendInteraction(message.getFrom(), "CHOOSE_ARTICLE_SEARCH");
        }
        else{
            ArticleHandler handler = new ArticleHandler(null);
            handler.setArticleType(ArticleType.TODOS);
            try{
                handler.processResponse(message, this);
            } catch(HttpException e){
                sendInternalErrorMsg(message.getFrom(), e);
            }

        }
    }

    public void register(Message message, String commandText) {
        Long chatId = message.getChatId();
        UserSession userSession = cacheService.getSession(chatId);
        if(userSession != null) {
            sendInteraction(message.getFrom(), "ALREADY_LOGGED_IN", userSession.getName());
        }
        else{
            commandsHandlerMap.remove(chatId);
            RegisterHandler handler = new RegisterHandler(chatId);
            commandsHandlerMap.put(chatId, handler);
            sendInteraction(message.getFrom(), "REGISTER_NAME");
        }


    }
    public void login(Message message, String commandText){
        Long chatId = message.getChatId();
        UserSession userSession = cacheService.getSession(chatId);
        if(userSession != null) {
            sendInteraction(message.getFrom(), "ALREADY_LOGGED_IN", userSession.getName());
        }
        else{
            commandsHandlerMap.remove(chatId);
            LoginHandler handler = new LoginHandler(chatId);
            commandsHandlerMap.put(chatId, handler);
            sendInteraction(message.getFrom(), "LOGIN_EMAIL");
        }
    }
    public void logout(Message message, String commandText){
        Long chatId = message.getChatId();
        UserSession userSession = cacheService.getSession(chatId);
        if(userSession != null) {
            logOutUser(chatId, userSession);
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

    public String getTranslatedMessage(org.telegram.telegrambots.meta.api.objects.User telegramUser, String interaction){
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
            System.out.println(System.getenv("RESOURCE_URL"));

            List<NotificationDTO> notifications = notificationApi.getPendingNotifications();
            if (notifications.isEmpty()){
                System.out.println("No hay notificaciones pendientes de envio.");
            }else{
                System.out.println("Enviando notificaciones pendientes...");
                for (NotificationDTO notification : notifications) {
                    UserSession notificationUserSession = new UserSession ();
                    notificationUserSession.setSessionId( notification.getSubscriber());
                    Long chatId = cacheService.getChatIdOfSession(notificationUserSession);
                    if (chatId == null) {
                        System.out.println("Chat ID is null for user session: " + notification.getSubscriber());
                        continue;  // Skip this notification
                    }
                    try {
                        String message = generateMessage(notification);
                        sendText(chatId, message);
                        //TODO Different Sessions Same Notifaction Id will try to mark it as read more than once
                        boolean marked = notificationApi.markAsNotified(notification.getId());
                        if (!marked) {
                            System.err.println("Failed to mark notification as notified: " + notification.getId());
                        }
                    } catch (Exception e) {
                        System.err.println("Error sending notification: " + e.getMessage());
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Error fetching pending notifications: " + e.getMessage());
        }
    }

    public String generateMessage(NotificationDTO notification) {
        String baseMessage;
        String subscriberInfo = "📊 Suscriptores actuales: " + notification.getCurrentSubscribers() + "\n" +
                "🔹 Cantidad mínima de suscriptores: " + notification.getMinSubscribers() + "\n" +
                "🔹 Cantidad máxima de suscriptores: " + notification.getMaxSubscribers() + ".";

        switch (notification.getType()) {
            case "ClosedArticleNotification":
                baseMessage = "🔒 El artículo \"" + notification.getArticleName() + "\" ha sido cerrado.\n";
                if (notification.getCurrentSubscribers() < notification.getMinSubscribers()) {
                    return baseMessage + subscriberInfo + "\n❗ No se ha llegado a la cantidad mínima de suscriptores, por lo que se cancela la operacion";
                }
                return baseMessage + subscriberInfo + "\n ✅ Ya se puede realizar la operacion";
            case "OwnerClosedArticleNotification":
                baseMessage = "🔒 Tu artículo \"" + notification.getArticleName() + "\" ha sido cerrado.\n";
                if (notification.getCurrentSubscribers() < notification.getMinSubscribers()) {
                    return baseMessage + subscriberInfo + "\n❗ No se ha llegado a la cantidad mínima de suscriptores.";
                }
                return baseMessage + subscriberInfo + "\n ✅ Ya se puede realizar la operacion";
            case "SubscriptionNotification":
                baseMessage = "✅ Un nuevo usuario se ha suscripto al artículo \"" + notification.getArticleName() + "\".\n";
                if (notification.getCurrentSubscribers() < notification.getMinSubscribers()) {
                    return baseMessage + subscriberInfo + "\n❗ Falta/n suscribirse " +
                            (notification.getMinSubscribers() - notification.getCurrentSubscribers()) + " usuario/s como mínimo.";
                }
                return baseMessage + subscriberInfo;
            case "OwnerSubscriptionNotification":
                baseMessage = "✅ Un usuario se ha suscripto a tu artículo \"" + notification.getArticleName() + "\".\n";
                if (notification.getCurrentSubscribers() < notification.getMinSubscribers()) {
                    return baseMessage + subscriberInfo + "\n❗ Falta/n suscribirse " +
                            (notification.getMinSubscribers() - notification.getCurrentSubscribers()) + " usuario/s como mínimo.";
                }
                return baseMessage + subscriberInfo;
            default:
                return "📬 Has recibido una nueva notificación en tu publicación: " + notification.getArticleName();
        }
    }





    public void resetUserHandlers(Long userId){
        commandsHandlerMap.remove(userId);
    }


    public void logOutUser(Long chatId, UserSession userSession){
        cacheService.deleteSessionMapping(chatId, userSession);
    }

    public void logInUser(Long chatId, UserSession userSession) throws IOException {
        cacheService. addSessionMapping(chatId, userSession);
    }
}