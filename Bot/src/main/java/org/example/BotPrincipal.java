package org.example;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class BotPrincipal extends TelegramLongPollingBot {

    @Override
    public String getBotUsername() {
        return "BotTACS";
    }

    @Override
    public String getBotToken() {
        return "7099560063:AAHHlzFtFDCCqUrbBmg_cyqmYg8qiXVI1j0";
    }

    public void onUpdateReceived(Update update) {
        var msg = update.getMessage();
        var user = msg.getFrom();
        var id = user.getId();
        if(msg.getText().equals("/listaarticulos")) {
            sendText(user.getId(), "Ver lista");
        }
        if(msg.getText().equals("/publicar")) {
            sendText(user.getId(), "Ver lista");
        }
        if(msg.getText().equals("/reporte")) {
            sendText(user.getId(), "Ver lista");
        }
    }

    public void sendText(Long who, String what){
        SendMessage sm = SendMessage.builder()
                .chatId(who.toString()) //Who are we sending a message to
                .text(what).build();    //Message content
        try {
            execute(sm);                        //Actually sending the message
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);      //Any error will be printed here
        }
    }
}
