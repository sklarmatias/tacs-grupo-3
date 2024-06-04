package org.tacsbot.bot;
import lombok.Setter;
import org.apache.http.HttpException;
import org.tacsbot.dictionary.JSONMessageDictionary;
import org.tacsbot.dictionary.MessageDictionary;
import org.tacsbot.handlers.impl.*;
import org.tacsbot.handlers.*;
import org.tacsbot.model.Article;
import org.tacsbot.model.User;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyTelegramBot extends TelegramLongPollingBot {

    @Setter
    private MessageDictionary messageDictionary;

    private final Map<String, CommandAction> commandActions = new HashMap<>();

    public final Map<Long, CommandsHandler> commandsHandlerMap = new HashMap<>();
    public final Map<Long, String> usersLoginMap = new HashMap<>();

    public final Map<Long, User> loggedUsersMap = new HashMap<>();

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

    }

    @Override
    public String getBotUsername() {
        return System.getenv("BOT_USERNAME");
    }

    public void sendInternalErrorMsg(Long chatId, Exception exception){
        sendText(chatId, "Tuvimos un error interno. Por favor. Volve a intentarlo más tarde!");
        System.out.printf("[Error] Error:\n%s\n", exception.getMessage());
        exception.printStackTrace();
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
                    sendInternalErrorMsg(id, e);
                }
            } else{
                sendInteraction(user, "UNKNOWN_COMMAND");
            }

        }else if (commandsHandlerMap.containsKey(id)){
            try {
                commandsHandlerMap.get(id).processResponse(msg, this);
            } catch (Exception e){
                sendInternalErrorMsg(id, e);
            }

        }else sendInteraction(user, "WELCOME");
    }

    // commands

    private void helpCommand(Message message, String commandText) {

        sendInteraction(message.getFrom(), "HELP");

    }

    private void createArticle(Message message, String commandText) {

        if(usersLoginMap.containsKey(message.getFrom().getId())) {
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
        if(usersLoginMap.containsKey(chatId)) {
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
                sendInternalErrorMsg(message.getChatId(), e);
            }

        }
    }

    private void register(Message message, String commandText) {
        Long chatId = message.getChatId();
        if(usersLoginMap.containsKey(chatId)){
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
        if(usersLoginMap.containsKey(chatId)){
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
        if(usersLoginMap.containsKey(chatId)){
            usersLoginMap.remove(chatId);
            loggedUsersMap.remove(chatId);
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

    public void resetUserHandlers(Long userId){
        commandsHandlerMap.remove(userId);
    }




}