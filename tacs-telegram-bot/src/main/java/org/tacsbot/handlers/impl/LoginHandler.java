package org.tacsbot.handlers.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.Response;
import org.tacsbot.model.User;
import org.tacsbot.handlers.CommandHandler;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.apache.cxf.jaxrs.client.WebClient;
import org.tacsbot.bot.MyTelegramBot;

public class LoginHandler implements CommandHandler {
    private Long chatId;
    private LoginStep currentStep;
    private String email;
    private String pass;

    @Override
    public void processResponse(Message message, MyTelegramBot bot) {
        switch (currentStep) {
            case REQUEST_EMAIL:
                // Step 1: Request the article's name
                email = message.getText();
                currentStep = LoginStep.REQUEST_PASSWORD;
                bot.sendText(chatId, "Por favor ingresa la clave:");
                break;
            case REQUEST_PASSWORD:
                pass = message.getText();
                String jsonrequest = "{\n" +
                        "  \"email\": \"" + email + "\",\n" +
                        "  \"pass\": \"" + pass + "\"\n" +
                        "}";
                WebClient client = WebClient.create(System.getenv("RESOURCE_URL") + "/users/login");
                System.out.println(jsonrequest);
                Response response = client.type("application/json").post(jsonrequest);
                System.out.println(response.getStatus());
                if(response.getStatus() == 200){
                    String userString = response.readEntity(String.class);
                    System.out.println(userString);
                    ObjectMapper mapper = new ObjectMapper();
                    User user = null;
                    try {
                        user = mapper.readValue(userString, User.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    bot.usersLoginMap.put(chatId,user.id);
                    System.out.println(user.id);
                    System.out.println(bot.usersLoginMap.get(chatId));
                    bot.sendText(chatId,"Se ha logueado el usuario " + user.id.toString());
                }
                else{
                    bot.sendText(chatId,"Credenciales incorrectas");
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
