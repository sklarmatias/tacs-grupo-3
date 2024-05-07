package org.tacsbot;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;



public class Main {
    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        BotPrincipal botPrincipal = new BotPrincipal();
        botPrincipal.usersLoginMap.put(6720253612L,6720253612L);
        botsApi.registerBot(botPrincipal);

    }
}