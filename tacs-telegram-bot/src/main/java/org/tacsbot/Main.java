package org.tacsbot;

import org.tacsbot.bot.MyTelegramBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;



public class Main {
    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        MyTelegramBot myTelegramBot = new MyTelegramBot();
        myTelegramBot.usersLoginMap.addMapping(6720253612L,"663d7b0ff6f83d1100463b5f");
        botsApi.registerBot(myTelegramBot);

    }
}