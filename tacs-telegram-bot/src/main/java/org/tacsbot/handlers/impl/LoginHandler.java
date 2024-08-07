package org.tacsbot.handlers.impl;

import lombok.Getter;
import lombok.Setter;
import org.tacsbot.api.user.UserApi;
import org.tacsbot.api.user.impl.UserApiConnection;
import org.tacsbot.handlers.CommandsHandler;
import org.tacsbot.helper.RegisterValidatorHelper;
import org.tacsbot.model.User;
import org.tacsbot.model.UserSession;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.tacsbot.bot.MyTelegramBot;
import javax.naming.AuthenticationException;
import java.io.IOException;
import java.net.URISyntaxException;

public class LoginHandler implements CommandsHandler {
    private final Long chatId;
    private LoginStep currentStep;
    @Getter
    private final User user;
    @Setter
    private UserApi userApiConnection = new UserApiConnection();

    @Override
    public void processResponse(Message message, MyTelegramBot bot) throws IOException, URISyntaxException, InterruptedException {
        switch (currentStep) {
            case REQUEST_EMAIL:
                if(RegisterValidatorHelper.validateEmail(message.getText()) != null){
                    bot.sendInteraction(message.getFrom(), "ERROR_EMAIL_INVALID");
                    bot.sendInteraction(message.getFrom(), "LOGIN_EMAIL");
                }
                else{
                    user.setEmail(message.getText().toLowerCase());
                    currentStep = LoginStep.REQUEST_PASSWORD;
                    bot.sendInteraction(message.getFrom(), "LOGIN_PASS");
                }
                break;
            case REQUEST_PASSWORD:
                user.setPass(message.getText());
                try{
                    UserSession savedUser = userApiConnection.logIn(user.getEmail(), user.getPass());
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
