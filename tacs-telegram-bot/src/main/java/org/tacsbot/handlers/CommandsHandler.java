package org.tacsbot.handlers;

import org.apache.http.HttpException;
import org.tacsbot.bot.MyTelegramBot;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.IOException;
import java.net.URISyntaxException;

public interface CommandsHandler {



    void processResponse(Message message, MyTelegramBot bot) throws IOException, HttpException;
}