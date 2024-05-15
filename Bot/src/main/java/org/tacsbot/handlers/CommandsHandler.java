package org.tacsbot.handlers;

import org.tacsbot.BotPrincipal;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface CommandsHandler {



    void procesarRespuesta(Message respuesta, BotPrincipal bot);
}