package org.tacsbot;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.Response;
import org.apache.cxf.jaxrs.client.WebClient;
import org.json.JSONArray;
import org.tacsbot.clases.Article;
import org.tacsbot.handlers.CommandAction;
import org.tacsbot.handlers.CommandsHandler;
import org.tacsbot.handlers.CrearArticuloHandler;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BotPrincipal extends TelegramLongPollingBot {

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



    private final Map<Long, CommandsHandler> commandsHandlerMap = new HashMap<>();



    public BotPrincipal() {
        // Inicialización del mapa commandActions con las acciones asociadas a los comandos
        commandActions.put("/crear_articulo", this::crearArticulo);
        commandActions.put("/obtener_articulos", this::obtenerArticulos);
        commandActions.put("/menu", this::mostrarMenu);
    }






    @Override
    public String getBotUsername() {
        return "TACS Bot";
    }

    @Override
    public String getBotToken() {
        return "6725740198:AAHp4qUGFKv8dAtVj1rCOPqoG-_1c3LcYFA";
    }

    @Override
    public void onUpdateReceived(Update update) {
        var msg = update.getMessage();
        var user = msg.getFrom();
        var id = user.getId();
        var txt = msg.getText();

        if (update.hasCallbackQuery()) {
            System.out.println("Se oprimio un boton");
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

            // Obtenemos la acción asociada al comando

            CommandAction action = commandActions.get(command);
            if (action != null) {
                // Ejecutamos la acción si está definida
                action.execute(id, txt);
            }

        }else if (commandsHandlerMap.containsKey(id)){
            commandsHandlerMap.get(id).procesarRespuesta(msg, this);

        }else{sendText(id,"Mensaje no valido");}

        System.out.println(user.getFirstName() + " wrote " + msg.getText() + " from " + user.getId());
    }

    // Método para crear un artículo
    private void crearArticulo(Long userId, String commandText) {
        // Aquí iría la lógica para crear y guardar el artículo en la base de datos
        //TODO
        CrearArticuloHandler handler = new CrearArticuloHandler(userId);
        commandsHandlerMap.put(userId, handler);
        sendText(userId, "Ingrese el nombre del articulo: ");

        //System.out.println("Artículo creado por el usuario " + userId);
    }

    // Método para obtener artículos
    private void obtenerArticulos(Long id, String commandText) {
        // Aca iría la lógica para obtener y mostrar los artículos al usuario
        WebClient client = WebClient.create("http://localhost:8080/restapp/articles");
        Response response = client.accept("application/json").get();
        sendText(id, "Estos son los articulos disponibles");
        sendText(id,parsearJson(response.readEntity(String.class)));
        System.out.println("Artículos obtenidos por el usuario " + id);
    }
    private String parsearJson(String json) {
        String result = "";
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<Article> articulos = mapper.readValue(json, new TypeReference<List<Article>>(){});
            for (Article art : articulos){
                result += art.getString() + "\n";
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return result;
    }
    // Método para mostrar el menú
    private void mostrarMenu(Long id, String commandText) {
        // Aca iría la lógica para mostrar el menú al usuario
        //TODO
        sendText(id, "Este es el menu");
        System.out.println("Menú mostrado al usuario " + id);
    }

    public void sendText(Long who, String what){
        SendMessage sm = SendMessage.builder()
                .chatId(who.toString()) //Who are we sending a message to
                .text(what).build();    //Message content
        try {
            this.execute(sm);                        //Actually sending the message
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);      //Any error will be printed here
        }
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