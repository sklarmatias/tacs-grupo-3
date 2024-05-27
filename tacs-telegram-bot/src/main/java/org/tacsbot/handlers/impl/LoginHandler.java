package org.tacsbot.handlers.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.tacsbot.api.user.impl.UserApiConnection;
import org.tacsbot.handlers.CommandsHandler;
import org.tacsbot.model.User;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.tacsbot.bot.MyTelegramBot;

import javax.naming.AuthenticationException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class LoginHandler implements CommandsHandler {
    private Long chatId;
    private LoginStep currentStep;
    private String email;
    private String pass;

    @Override
    public void processResponse(Message message, MyTelegramBot bot) throws IOException {
        switch (currentStep) {
            case REQUEST_EMAIL:
                email = message.getText();
                currentStep = LoginStep.REQUEST_PASSWORD;
                bot.sendText(chatId, "Por favor ingresa la clave:");
                break;
            case REQUEST_PASSWORD:
                pass = message.getText();
                try{
                    User user = new UserApiConnection().logIn(email, pass);
                    bot.usersLoginMap.put(chatId,user.getId());
                    bot.loggedUsersMap.put(chatId, user);
                    System.out.println(user.getId());
                    System.out.println(bot.usersLoginMap.get(chatId));
                    bot.sendText(chatId,
                            String.format("Hola %s! Un gusto verte por acá. Recordá que podes consultar los comandos disponibles ingresando /help.",
                                    user.getName()));
                } catch (AuthenticationException e){
                    bot.sendText(chatId,"Credenciales incorrectas. Para intentarlo devuelta, ingresá /login.");
                }
                break;
        }
    }

    private enum LoginStep {
        REQUEST_EMAIL,
        REQUEST_PASSWORD
    }

    public LoginHandler(Long userId) {
        this.chatId = userId;
        this.currentStep = LoginStep.REQUEST_EMAIL;
    }
}
