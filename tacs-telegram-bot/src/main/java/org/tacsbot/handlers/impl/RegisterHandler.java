package org.tacsbot.handlers.impl;

import org.tacsbot.bot.MyTelegramBot;
import org.tacsbot.handlers.CommandsHandler;
import org.tacsbot.helper.RegisterValidatorHelper;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RegisterHandler implements CommandsHandler {
    private Long chatId;

    private String userName;

    private String userSurname;

    private String userEmail;

    private String userPassword;

    private RegistrationStep currentStep;

    public RegisterHandler(Long chatId) {
        this.chatId = chatId;
        this.currentStep = RegistrationStep.REQUEST_USER_NAME;
    }


    private enum RegistrationStep {
        REQUEST_USER_NAME,
        REQUEST_USER_SURNAME,
        REQUEST_EMAIL,
        REQUEST_PASSWORD,
    }

    @Override
    public void processResponse(Message message, MyTelegramBot bot) throws URISyntaxException, IOException, InterruptedException {
        String errorMessage = null;
        switch (currentStep) {

            case REQUEST_USER_NAME:
                errorMessage = RegisterValidatorHelper.validateUserName(message.getText());
                if (errorMessage == null){
                    this.userName = message.getText();

                    this.currentStep = RegistrationStep.REQUEST_USER_SURNAME;
                    bot.sendText(chatId, "Por favor ingrese su apellido");
                }else {
                    bot.sendText(chatId, errorMessage + "Ingrese un nombre nuevamente...");
                }
                break;
            case REQUEST_USER_SURNAME:
                errorMessage = RegisterValidatorHelper.validateUserSurname(message.getText());
                if (errorMessage == null){
                    this.userSurname = message.getText();
                    this.currentStep = RegistrationStep.REQUEST_EMAIL;
                    bot.sendText(chatId, "Por favor ingrese su mail");
                }else {
                    bot.sendText(chatId, errorMessage + "Ingrese un apellido nuevamente...");
                }




                break;
            case REQUEST_EMAIL:
                errorMessage = RegisterValidatorHelper.validateEmail(message.getText());
                if (errorMessage == null){
                    this.userEmail = message.getText().toLowerCase();
                    this.currentStep = RegistrationStep.REQUEST_PASSWORD;
                    bot.sendText(chatId, "Por favor ingrese su contraseña:");
                }else {
                    bot.sendText(chatId, errorMessage + "Ingrese un mail nuevamente...");
                }


                break;
            case REQUEST_PASSWORD:
                errorMessage = RegisterValidatorHelper.validatePassword(message.getText());
                if (errorMessage == null){
                    this.userPassword = message.getText();
                    this.currentStep = RegistrationStep.REQUEST_PASSWORD;
                    bot.sendText(chatId, "Registrando usuario...");
                    String jsonrequest = "{\n" +
                            "  \"name\": \"" + this.userName + "\",\n" +
                            "  \"surname\": \"" + this.userSurname + "\",\n" +
                            "  \"email\": \"" + this.userEmail + "\",\n" +
                            "  \"pass\": \"" + this.userPassword + "\"\n" +
                            "}";
                    System.out.println(jsonrequest);
                        HttpClient client = HttpClient.newHttpClient();
                    HttpRequest request = HttpRequest.newBuilder()
                                .uri(new URI(System.getenv("RESOURCE_URL") + "/users/register"))
                                .POST(HttpRequest.BodyPublishers.ofString(jsonrequest))
                                .header("Content-Type","application/json")
                                .build();
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    System.out.println(response.statusCode());
                        System.out.println(response.body());
                        if (response.statusCode() == 201) {
                            bot.sendText(chatId, "Usario creado correctamente");
                        }
                    }

                else {
                    bot.sendText(chatId, "Error al registrar usuario, intente nuevamente mas tarde.");
                }

                break;
            default:
                throw new IllegalStateException("Unexpected value: " + currentStep);
        }





    }
}
