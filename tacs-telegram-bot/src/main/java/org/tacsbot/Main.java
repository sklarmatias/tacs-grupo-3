package org.tacsbot;

import org.tacsbot.bot.MyTelegramBot;
import org.tacsbot.model.User;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.IOException;


public class Main {
    public static void main(String[] args) throws TelegramApiException, IOException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        MyTelegramBot myTelegramBot = new MyTelegramBot();
        myTelegramBot.logInUser(6720253612L,new User("665f6686f10aef2e086b9254", null, null, null, null));
        myTelegramBot.logInUser(7354771796L,new User("665f66f4f10aef2e086b9256", null, null, null, null));
        botsApi.registerBot(myTelegramBot);
        myTelegramBot.scheduleNotificationChecks();

    }
}