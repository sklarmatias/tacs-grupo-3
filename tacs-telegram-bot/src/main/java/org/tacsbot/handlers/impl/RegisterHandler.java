package org.tacsbot.handlers.impl;

import org.tacsbot.api.user.impl.UserApiConnection;
import org.tacsbot.bot.MyTelegramBot;
import org.tacsbot.handlers.CommandsHandler;
import org.tacsbot.helper.RegisterValidatorHelper;
import org.telegram.telegrambots.meta.api.objects.Message;
import java.io.IOException;

public class RegisterHandler implements CommandsHandler {
    private Long chatId;

    private String userName;

    private String userSurname;

    private String userEmail;

    private RegistrationStep currentStep;

    private UserApiConnection userApiConnection;

    public RegisterHandler(Long chatId) {
        this.chatId = chatId;
        this.currentStep = RegistrationStep.REQUEST_USER_NAME;
        userApiConnection = new UserApiConnection();
    }


    private enum RegistrationStep {
        REQUEST_USER_NAME,
        REQUEST_USER_SURNAME,
        REQUEST_EMAIL,
        REQUEST_PASSWORD,
    }

    private void register(String name, String surname, String email, String password, Message message, MyTelegramBot bot){
        try {
            bot.sendInteraction(message.getFrom(), "LOADING");
            userApiConnection.register(name, surname, email, password);
            bot.sendInteraction(message.getFrom(), "REGISTER_COMPLETED");

        } catch (IOException e) {
            bot.sendInternalErrorMsg(message.getChatId(), e);
        }
    }

    @Override
    public void processResponse(Message message, MyTelegramBot bot) {
        String errorMessage;
        switch (currentStep) {

            case REQUEST_USER_NAME:
                errorMessage = RegisterValidatorHelper.validateUserName(message.getText());
                if (errorMessage == null){
                    this.userName = message.getText();

                    this.currentStep = RegistrationStep.REQUEST_USER_SURNAME;
                    bot.sendInteraction(message.getFrom(), "REGISTER_SURNAME");
                }else {
                    bot.sendText(chatId, errorMessage + "Ingrese un nombre nuevamente...");
                }
                break;
            case REQUEST_USER_SURNAME:
                errorMessage = RegisterValidatorHelper.validateUserSurname(message.getText());
                if (errorMessage == null){
                    this.userSurname = message.getText();
                    this.currentStep = RegistrationStep.REQUEST_EMAIL;
                    bot.sendInteraction(message.getFrom(), "REGISTER_EMAIL");
                }else {
                    bot.sendText(chatId, errorMessage + "Ingrese un apellido nuevamente...");
                }




                break;
            case REQUEST_EMAIL:
                errorMessage = RegisterValidatorHelper.validateEmail(message.getText());
                if (errorMessage == null){
                    this.userEmail = message.getText().toLowerCase();
                    this.currentStep = RegistrationStep.REQUEST_PASSWORD;
                    bot.sendInteraction(message.getFrom(),"REGISTER_PASS");
                }else {
                    bot.sendText(chatId, errorMessage + "Ingrese un mail nuevamente...");
                }


                break;
            case REQUEST_PASSWORD:
                String userPassword = message.getText();
                register(userName, userSurname, userEmail, userPassword,message, bot);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + currentStep);
        }





    }
}
