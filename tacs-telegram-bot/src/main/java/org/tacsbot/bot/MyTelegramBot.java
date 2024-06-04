package org.tacsbot.bot;
import org.apache.http.HttpException;
import org.tacsbot.api.notification.NotificationApi;
import org.tacsbot.api.notification.impl.NotificationApiConnection;
import org.tacsbot.handlers.impl.*;
import org.tacsbot.handlers.*;
import org.tacsbot.model.NotificationDTO;
import org.tacsbot.model.UserChatMapping;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.CopyMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MyTelegramBot extends TelegramLongPollingBot {

    private final Map<String, CommandAction> commandActions = new HashMap<>();
    InlineKeyboardButton next = InlineKeyboardButton.builder()
            .text("Next").callbackData("next")
            .build();

    InlineKeyboardButton back = InlineKeyboardButton.builder()
            .text("Back").callbackData("back")
            .build();

    InlineKeyboardButton url = InlineKeyboardButton.builder()
            .text("Tutorial")
            .url("https://core.telegram.org/bots/api")
            .build();

    private InlineKeyboardMarkup keyboardM1 = InlineKeyboardMarkup.builder()
            .keyboardRow(List.of(next)).build();

    //Buttons are wrapped in lists since each keyboard is a set of button rows

    private InlineKeyboardMarkup keyboardM2 = InlineKeyboardMarkup.builder()
            .keyboardRow(List.of(back))
            .keyboardRow(List.of(url))
            .build();;



    public final Map<Long, CommandsHandler> commandsHandlerMap = new HashMap<>();
    public final UserChatMapping usersLoginMap = new UserChatMapping();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final NotificationApi notificationApi = new NotificationApiConnection();


    public MyTelegramBot() {
        super(System.getenv("BOT_TOKEN"));
        // Initialization of the commandActions map with the actions associated with the commands
        commandActions.put("/crear_articulo", this::createArticle);
        commandActions.put("/obtener_articulos", this::searchArticles);
//        commandActions.put("/ver_anotados", this::seeSignUpsInArticle);
        commandActions.put("/menu", this::showMenu);
        commandActions.put("/login", this::login);
        commandActions.put("/logout", this::logout);
        commandActions.put("/registrarme", this::register);
    }


    @Override
    public String getBotUsername() {
        return System.getenv("BOT_USERNAME");
    }

    @Override
    public void onUpdateReceived(Update update) {
        var msg = update.getMessage();
        var user = msg.getFrom();
        var id = user.getId();
        var txt = msg.getText();
        System.out.println(user.getFirstName() + " wrote " + msg.getText() + " from " + user.getId());
        if (update.hasCallbackQuery()) {
            System.out.println("A button was pressed");
            var callbackQuery = update.getCallbackQuery();
            var queryData = callbackQuery.getData();
            var queryId = callbackQuery.getId();
            var messageId = callbackQuery.getMessage().getMessageId();


            try {
                // Call the buttonTap function with the necessary parameters
                System.out.println("Se oprimio un boton");
                buttonTap(id, queryId, queryData, messageId);
            } catch (TelegramApiException e) {
                // Handle any exceptions that may occur when executing the function.
                e.printStackTrace();
            }
        }



        if(msg.isCommand()){
            resetUserHandlers(id);
            String command = msg.getText();

            // Obtain the action associated with the command
            if (command.equals("/help")){
                sendText(id,
                        "/crear_articulo - Crear un articulo\n" +
                        "/obtener_articulos - Ver artículos\n" +
                        "/login - Iniciar sesión\n" +
                        "/logout - Cerrar sesión\n" +
                        "/registrarme - Registrarte como un usuario!");
            }else {
                try{
                    CommandAction action = commandActions.get(command);
                    if (action != null) {
                        // Execute the action if it is defined
                        action.execute(id, txt);
                    }
                } catch (Exception e){
                    sendText(id, "Tuvimos un error interno. Por favor. Volve a intentarlo más tarde!");
                    System.out.printf("[Error] Uncaught error:\n%s\n", e.getMessage());
                }
            }




        }else if (commandsHandlerMap.containsKey(id)){
            try {
                commandsHandlerMap.get(id).processResponse(msg, this);
            } catch (HttpException | IOException e){
                sendText(id, "Tuvimos un error interno. Por favor. Volve a intentarlo más tarde!");
                System.out.println(e.getMessage());
                e.printStackTrace();
            } catch (Exception e){
                sendText(id, "Tuvimos un error interno. Por favor. Volve a intentarlo más tarde!");
                System.out.printf("[Error] Uncaught error:\n%s\n", e.getMessage());
                e.printStackTrace();
            }

        }else{sendText(id,"Hola, bienvenido! Para visualizar los comandos disponibles ingrese /help");}
    }


    private void createArticle(Long chatId, String commandText) {

        if(usersLoginMap.containsChatIdKey(chatId)) {
            System.out.println("User is logged in");
            commandsHandlerMap.remove(chatId);
            ArticleCreationHandler handler = new ArticleCreationHandler(chatId);
            commandsHandlerMap.put(chatId, handler);
            sendText(chatId, "Ingrese el nombre del articulo: ");
        }
        else{

            System.out.println();
            sendText(chatId, "Para crear artículos, tenés que iniciar sesión \uD83E\uDD13.");
            sendText(chatId, "Para iniciar sesión, escribí /login y seguí los pasos!.");
            sendText(chatId, "Para registrarte, podes hacerlo con /registrarme.");
        }

    }


    private void searchArticles(Long chatId, String commandText) {

        if(usersLoginMap.containsChatIdKey(chatId)) {
            commandsHandlerMap.remove(chatId);
            ArticleHandler handler = new ArticleHandler(chatId);
            commandsHandlerMap.put(chatId, handler);
            sendText(chatId, "Desea ver sus articulos (PROPIOS) o todos (TODOS):");
        }
        else{
            ArticleHandler handler = new ArticleHandler(chatId);
            handler.setArticleType(ArticleType.TODOS);
            try{
                handler.processResponse(null, this);
            } catch(HttpException e){
                sendText(chatId, "Ha ocurrido un error. Vuelve a intentarlo más tarde.");
            }

        }
    }

    private void register(Long chatId, String commandText) {
        if(usersLoginMap.containsChatIdKey(chatId)){
            sendText(chatId, "Hola! Ya iniciaste sesión, ingresá /logout para cerrar sesión y poder crear un nuevo usuario.");
        }
        else{
            commandsHandlerMap.remove(chatId);
            RegisterHandler handler = new RegisterHandler(chatId);
            commandsHandlerMap.put(chatId, handler);
            sendText(chatId, "Ingrese su nombre: ");
        }


    }
    private void login(Long chatId, String commandText){
        if(usersLoginMap.containsChatIdKey(chatId)){
            sendText(chatId, "Ya se encuentra logueado");
        }
        else{
            commandsHandlerMap.remove(chatId);
            LoginHandler handler = new LoginHandler(chatId);
            commandsHandlerMap.put(chatId, handler);
            sendText(chatId, "Ingrese su mail: ");
        }
    }
    private void logout(Long chatId, String commandText){
        if(usersLoginMap.containsChatIdKey(chatId)){
            usersLoginMap.removeByChatId(chatId);
            sendText(chatId, "Se ha deslogueado");
        }
        else{
            sendText(chatId, "No se encuentra logueado");
        }

    }

    private void showMenu(Long id, String commandText) {
        // Aca iría la lógica para mostrar el menú al usuario
        //TODO
        sendText(id, "Este es el menu");
        System.out.println("Menú mostrado al usuario " + id);
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
    public void sendTextHtml(Long who, String what){
        SendMessage sm = SendMessage.builder()
                .chatId(who.toString()) //Who are we sending a message to
                .parseMode("HTML")
                .text(what).build();    //Message content
        try {
            this.execute(sm);                        //Actually sending the message
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);      //Any error will be printed here
        }
    }

    public void copyMessage(Long who, Integer msgId){
        CopyMessage cm = CopyMessage.builder()
                .fromChatId(who.toString())  //We copy from the user
                .chatId(who.toString())      //And send it back to him
                .messageId(msgId)            //Specifying what message
                .build();
        try {
            this.execute(cm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
    public void sendMenu(Long who, String txt, InlineKeyboardMarkup kb){
        SendMessage sm = SendMessage.builder().chatId(who.toString())
                .parseMode("HTML").text(txt).build();

        try {
            execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void buttonTap(Long id, String queryId, String data, int msgId) throws TelegramApiException {

        EditMessageText newTxt = EditMessageText.builder()
                .chatId(id.toString())
                .messageId(msgId).text("").build();

        EditMessageReplyMarkup newKb = EditMessageReplyMarkup.builder()
                .chatId(id.toString()).messageId(msgId).build();

        if(data.equals("next")) {
            newTxt.setText("MENU 2");
            newKb.setReplyMarkup(keyboardM2);
        } else if(data.equals("back")) {
            newTxt.setText("MENU 1");
            newKb.setReplyMarkup(keyboardM1);
        }

        AnswerCallbackQuery close = AnswerCallbackQuery.builder()
                .callbackQueryId(queryId).build();

        execute(close);
        execute(newTxt);
        execute(newKb);
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
        switch (notification.getType()) {
            case "ClosedArticleNotification":
                return "El artículo \"" + notification.getArticleName() + "\" ha sido cerrado.";
            case "OwnerClosedArticleNotification":
                return "Tu artículo \"" + notification.getArticleName() + "\" ha sido cerrado.";
            case "SubscriptionNotification":
                return "Te has suscrito al artículo \"" + notification.getArticleName() + "\".";
            case "OwnerSubscriptionNotification":
                return "Alguien se ha suscrito a tu artículo \"" + notification.getArticleName() + "\".";
            default:
                return "Tienes una nueva notificación sobre el artículo \"" + notification.getArticleName() + "\".";
        }
    }


    public void resetUserHandlers(Long userId){
        commandsHandlerMap.remove(userId);
    }




}