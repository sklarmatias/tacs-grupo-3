package org.tacsbot.bot;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.tacsbot.handlers.impl.RegisterHandler;
import org.tacsbot.model.Article;
import org.tacsbot.handlers.*;
import org.tacsbot.handlers.impl.ArticleCreationHandler;
import org.tacsbot.handlers.impl.ArticleHandler;
import org.tacsbot.handlers.impl.LoginHandler;
import org.tacsbot.model.User;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public final Map<Long, String> usersLoginMap = new HashMap<>();

    public final Map<Long, User> loggedUsersMap = new HashMap<>();

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
                CommandAction action = commandActions.get(command);
                if (action != null) {
                    // Execute the action if it is defined
                    action.execute(id, txt);
                }
            }




        }else if (commandsHandlerMap.containsKey(id)){
            try {
                commandsHandlerMap.get(id).processResponse(msg, this);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }else{sendText(id,"Hola, bienvenido! Para visualizar los comandos disponibles ingrese /help");}

        System.out.println(user.getFirstName() + " wrote " + msg.getText() + " from " + user.getId());
    }


    private void createArticle(Long chatId, String commandText) {

        if(usersLoginMap.containsKey(chatId)) {
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

        if(usersLoginMap.containsKey(chatId)) {
            commandsHandlerMap.remove(chatId);
            ArticleHandler handler = new ArticleHandler(chatId);
            commandsHandlerMap.put(chatId, handler);
            sendText(chatId, "Desea ver sus articulos (PROPIOS) o todos (TODOS):");
        }
        else{
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI(System.getenv("RESOURCE_URL") + "/articles"))
                        .GET()
                        .build();
                HttpClient client = HttpClient.newHttpClient();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                sendText(chatId, "Estos son los artículos:");
                sendText(chatId, parseJson(response.body()), true);
            }
            catch (Exception ex){
                System.out.println(ex.getMessage());

            }

        }
    }

    private void register(Long chatId, String commandText) {
        if(usersLoginMap.containsKey(chatId)){
            User u = loggedUsersMap.get(chatId);
            sendText(chatId, String.format("Hola %s! Ya iniciaste sesión, ingresá /logout para cerrar sesión y poder crear un nuevo usuario.",
                    u.name));
        }
        else{
            commandsHandlerMap.remove(chatId);
            RegisterHandler handler = new RegisterHandler(chatId);
            commandsHandlerMap.put(chatId, handler);
            sendText(chatId, "Ingrese su nombre: ");
        }


    }
    private void login(Long chatId, String commandText){
        if(usersLoginMap.containsKey(chatId)){
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
        if(usersLoginMap.containsKey(chatId)){
            usersLoginMap.remove(chatId);
            loggedUsersMap.remove(chatId);
            sendText(chatId, "Se ha deslogueado");
        }
        else{
            sendText(chatId, "No se encuentra logueado");
        }

    }

    public String parseJson(String json) {
        StringBuilder result = new StringBuilder();
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<Article> articles = mapper.readValue(json, new TypeReference<List<Article>>(){});
            System.out.println("Primer Id: " + articles.getFirst().getId());

            for (int i = 0; i < articles.size(); i++) {
                Article art = articles.get(i);
                result.append("*INDICE:* ").append(i).append("\n");
                result.append(art.getDetailedString());
                if (i < articles.size() - 1) {
                    result.append("\n\n").append("__________________________").append("\n\n\n");
                }
            }
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

        return result.toString();
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

    public void resetUserHandlers(Long userId){
        commandsHandlerMap.remove(userId);
    }




}