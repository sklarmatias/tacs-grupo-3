package org.tacsbot.handlers;

import org.tacsbot.bot.MyTelegramBot;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface CommandHandler {



    void processResponse(Message message, MyTelegramBot bot);
}