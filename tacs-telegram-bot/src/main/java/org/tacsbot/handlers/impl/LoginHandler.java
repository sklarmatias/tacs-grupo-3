package org.tacsbot.handlers.impl;

import org.tacsbot.api.user.impl.UserApiConnection;
import org.tacsbot.handlers.CommandsHandler;
import org.tacsbot.model.User;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.tacsbot.bot.MyTelegramBot;
import javax.naming.AuthenticationException;
import java.io.IOException;

public class LoginHandler implements CommandsHandler {
    private final Long chatId;
    private LoginStep currentStep;
    private final User user;

    @Override
    public void processResponse(Message message, MyTelegramBot bot) throws IOException {
        switch (currentStep) {
            case REQUEST_EMAIL:
                user.setEmail(message.getText().toLowerCase());
                currentStep = LoginStep.REQUEST_PASSWORD;
                bot.sendInteraction(message.getFrom(), "LOGIN_PASS");
                break;
            case REQUEST_PASSWORD:
                user.setPass(message.getText().toLowerCase());
                try{
                    User savedUser = new UserApiConnection().logIn(user.getEmail(), user.getPass());
                    bot.logInUser(chatId, savedUser);
                    bot.sendInteraction(message.getFrom(), "WELCOME_LOGGED_IN", savedUser.getName());
                } catch (AuthenticationException e){
                    bot.sendInteraction(message.getFrom(), "WRONG_CREDENTIALS");
                }
                break;
        }
    }

    private enum LoginStep {
        REQUEST_EMAIL,
        REQUEST_PASSWORD
    }

    public LoginHandler(Long userId) {
        this.user = new User();
        this.chatId = userId;
        this.currentStep = LoginStep.REQUEST_EMAIL;
    }
}
