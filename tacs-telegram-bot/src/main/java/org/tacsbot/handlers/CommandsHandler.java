package org.tacsbot.handlers;

import org.tacsbot.MyTelegramBot;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface CommandsHandler {



    void processResponse(Message message, MyTelegramBot bot);
}