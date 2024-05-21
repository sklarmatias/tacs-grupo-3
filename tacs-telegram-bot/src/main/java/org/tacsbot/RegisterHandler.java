package org.tacsbot;

import jakarta.ws.rs.core.Response;
import org.apache.cxf.jaxrs.client.WebClient;
import org.tacsbot.handlers.ArticleCreationHandler;
import org.tacsbot.handlers.CommandsHandler;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Locale;

public class RegisterHandler implements CommandsHandler {
    private Long chatId;

    private String userName;

    private String userSurname;

    private String userEmail;

    private String userPassword;

    private RegistrationStep currentStep;

    RegisterHandler(Long chatId) {
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
    public void processResponse(Message message, MyTelegramBot bot) {
        String errorMessage = null;
        switch (currentStep) {

            case REQUEST_USER_NAME:
                errorMessage = Validator.validateUserName(message.getText());
                if (errorMessage == null){
                    this.userName = message.getText();

                    this.currentStep = RegistrationStep.REQUEST_USER_SURNAME;
                    bot.sendText(chatId, "Por favor ingrese su apellido");
                }else {
                    bot.sendText(chatId, errorMessage + "Ingrese un nombre nuevamente...");
                }
                break;
            case REQUEST_USER_SURNAME:
                errorMessage = Validator.validateUserSurname(message.getText());
                if (errorMessage == null){
                    this.userSurname = message.getText();
                    this.currentStep = RegistrationStep.REQUEST_EMAIL;
                    bot.sendText(chatId, "Por favor ingrese su mail");
                }else {
                    bot.sendText(chatId, errorMessage + "Ingrese un apellido nuevamente...");
                }




                break;
            case REQUEST_EMAIL:
                errorMessage = Validator.validateEmail(message.getText());
                if (errorMessage == null){
                    this.userEmail = message.getText().toLowerCase();
                    this.currentStep = RegistrationStep.REQUEST_PASSWORD;
                    bot.sendText(chatId, "Por favor ingrese su contraseña:");
                }else {
                    bot.sendText(chatId, errorMessage + "Ingrese un mail nuevamente...");
                }


                break;
            case REQUEST_PASSWORD:
                errorMessage = Validator.validatePassword(message.getText());
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
                    WebClient client = WebClient.create(System.getenv("RESOURCE_URL") + "/users/register");
                    System.out.println(jsonrequest);
                    Response response = client.type("application/json").post(jsonrequest);
                    System.out.println(response.getStatus());
                    System.out.println(response.readEntity(String.class));
                    if (response.getStatus() == 201) {
                        bot.sendText(chatId, "Usario creado correctamente");
                    }
                }else {
                    bot.sendText(chatId, "Error al registrar usuario, intente nuevamente mas tarde.");
                }

                break;
            default:
                throw new IllegalStateException("Unexpected value: " + currentStep);
        }





    }
}
