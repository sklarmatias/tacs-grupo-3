package org.tacsbot.handlers;

import org.apache.http.HttpException;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.IOException;

@FunctionalInterface
public interface CommandAction {
    void execute(Message message, String commandText) throws HttpException, IOException;
}
