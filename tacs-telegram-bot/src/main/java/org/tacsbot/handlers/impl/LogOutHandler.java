package org.tacsbot.handlers.impl;

import lombok.Setter;
import org.apache.http.HttpException;
import org.tacsbot.api.utils.ApiHttpConnector;
import org.tacsbot.bot.MyTelegramBot;
import org.tacsbot.exceptions.UnauthorizedException;
import org.tacsbot.handlers.CommandsHandler;
import org.tacsbot.model.User;
import org.tacsbot.model.UserSession;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;

public class LogOutHandler implements CommandsHandler {

    private UserSession session;

    @Setter
    private ApiHttpConnector apiHttpConnector;

    public LogOutHandler(UserSession userSession){
        this(userSession, new ApiHttpConnector());
    }

    public LogOutHandler(UserSession userSession, ApiHttpConnector apiHttpConnector){
        this.session = userSession;
        this.apiHttpConnector = apiHttpConnector;
    }

    @Override
    public void processResponse(Message message, MyTelegramBot bot) throws IOException, HttpException, UnauthorizedException, URISyntaxException, InterruptedException {
        HttpResponse<String> response = apiHttpConnector.delete("/sessions", session.getSessionId());
        if (response.statusCode() == 200){
            bot.getCacheService().deleteSessionMapping(message.getChatId(), session);
            bot.sendInteraction(message.getFrom(), "LOG_OUT");
        }
        else throw new RuntimeException();

    }
}
