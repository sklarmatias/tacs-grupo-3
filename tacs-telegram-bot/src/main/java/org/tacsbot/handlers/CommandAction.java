package org.tacsbot.handlers;

import org.apache.http.HttpException;
import org.tacsbot.exceptions.UnauthorizedException;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.IOException;
import java.net.URISyntaxException;

@FunctionalInterface
public interface CommandAction {
    void execute(Message message, String commandText) throws HttpException, IOException, UnauthorizedException, URISyntaxException, InterruptedException;
}
