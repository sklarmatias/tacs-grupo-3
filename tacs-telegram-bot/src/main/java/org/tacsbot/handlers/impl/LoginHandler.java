package org.tacsbot.handlers.impl;

import org.tacsbot.api.user.impl.UserApiConnection;
import org.tacsbot.handlers.CommandsHandler;
import org.tacsbot.model.User;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.tacsbot.bot.MyTelegramBot;
import javax.naming.AuthenticationException;
import java.io.IOException;

public class LoginHandler implements CommandsHandler {
    private Long chatId;
    private LoginStep currentStep;
    private String email;
    private String pass;

    @Override
    public void processResponse(Message message, MyTelegramBot bot) throws IOException {
        switch (currentStep) {
            case REQUEST_EMAIL:
                email = message.getText().toLowerCase();
                currentStep = LoginStep.REQUEST_PASSWORD;
                bot.sendInteraction(message.getFrom(), "LOGIN_PASS");
                break;
            case REQUEST_PASSWORD:
                pass = message.getText();
                try{
                    User user = new UserApiConnection().logIn(email, pass);
                    bot.usersLoginMap.put(chatId,user.getId());
                    bot.loggedUsersMap.put(chatId, user);
                    System.out.println(user.getId());
                    System.out.println(bot.usersLoginMap.get(chatId));
                    bot.sendInteraction(message.getFrom(), "WELCOME_LOGGED_IN", user.getName());
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
        this.chatId = userId;
        this.currentStep = LoginStep.REQUEST_EMAIL;
    }
}
