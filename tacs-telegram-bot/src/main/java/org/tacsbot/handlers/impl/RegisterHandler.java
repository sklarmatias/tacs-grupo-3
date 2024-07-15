package org.tacsbot.handlers.impl;

import lombok.Getter;
import lombok.Setter;
import org.tacsbot.api.user.UserApi;
import org.tacsbot.api.user.impl.UserApiConnection;
import org.tacsbot.bot.MyTelegramBot;
import org.tacsbot.handlers.CommandsHandler;
import org.tacsbot.helper.RegisterValidatorHelper;
import org.tacsbot.model.User;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.IOException;

public class RegisterHandler implements CommandsHandler {
    private final Long chatId;
    @Getter
    private final User user;
    @Setter
    private RegistrationStep currentStep;

    @Setter
    private UserApi userApiConnection;

    public RegisterHandler(Long chatId) {
        this.user = new User();
        this.chatId = chatId;
        this.currentStep = RegistrationStep.REQUEST_USER_NAME;
        userApiConnection = new UserApiConnection();
    }


    public enum RegistrationStep {
        REQUEST_USER_NAME,
        REQUEST_USER_SURNAME,
        REQUEST_EMAIL,
        REQUEST_PASSWORD,
    }

    private void register(Message message, MyTelegramBot bot){
        try {
            bot.sendInteraction(message.getFrom(), "LOADING");
            try{
                System.out.println(user.getName());
                System.out.println(user.getPass());
                System.out.println(user.getSurname());
                System.out.println(user.getEmail());
                userApiConnection.register(user.getName(), user.getSurname(), user.getEmail(), user.getPass());
                bot.sendInteraction(message.getFrom(), "REGISTER_COMPLETED");
            } catch (IllegalArgumentException e){
                bot.sendInteraction(message.getFrom(), "REGISTER_INVALID");
            }

        } catch (IOException e) {
            bot.sendInternalErrorMsg(message.getFrom(), e);
            bot.sendInteraction(message.getFrom(), "REGISTER_INVALID");
        }
    }

    @Override
    public void processResponse(Message message, MyTelegramBot bot) {
        String errorMessage;
        switch (currentStep) {

            case REQUEST_USER_NAME:
                if (message.getText() != null && !message.getText().isEmpty()){
                    user.setName(message.getText());

                    this.currentStep = RegistrationStep.REQUEST_USER_SURNAME;
                    bot.sendInteraction(message.getFrom(), "REGISTER_SURNAME");
                }else {
                    bot.sendInteraction(message.getFrom(), "REGISTER_NAME_EMPTY");
                    bot.sendInteraction(message.getFrom(), "REGISTER_NAME");
                }
                break;
            case REQUEST_USER_SURNAME:
                if (message.getText() != null && !message.getText().isEmpty()){
                    user.setSurname(message.getText());
                    this.currentStep = RegistrationStep.REQUEST_EMAIL;
                    bot.sendInteraction(message.getFrom(), "REGISTER_EMAIL");
                }else {
                    bot.sendInteraction(message.getFrom(), "REGISTER_SURNAME_EMPTY");
                    bot.sendInteraction(message.getFrom(), "REGISTER_SURNAME");
                }
                break;
            case REQUEST_EMAIL:
                errorMessage = RegisterValidatorHelper.validateEmail(message.getText());
                if (errorMessage == null){
                    user.setEmail(message.getText().toLowerCase());
                    this.currentStep = RegistrationStep.REQUEST_PASSWORD;
                    bot.sendInteraction(message.getFrom(),"REGISTER_PASS");
                }else {
                    bot.sendInteraction(message.getFrom(), errorMessage);
                    bot.sendInteraction(message.getFrom(), "REGISTER_EMAIL");
                }


                break;
            case REQUEST_PASSWORD:
                user.setPass(message.getText());
                register(message, bot);
                break;
        }





    }
}
