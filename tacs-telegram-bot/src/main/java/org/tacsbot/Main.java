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
        botsApi.registerBot(myTelegramBot);
        myTelegramBot.scheduleNotificationChecks();

    }
}