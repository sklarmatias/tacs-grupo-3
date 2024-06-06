package org.tacsbot;

import org.tacsbot.bot.MyTelegramBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;



public class Main {
    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        MyTelegramBot myTelegramBot = new MyTelegramBot();
        myTelegramBot.usersLoginMap.addMapping(6720253612L,"665f6686f10aef2e086b9254");
        myTelegramBot.usersLoginMap.addMapping(7354771796L,"665f66f4f10aef2e086b9256");
        botsApi.registerBot(myTelegramBot);
        myTelegramBot.scheduleNotificationChecks();

    }
}