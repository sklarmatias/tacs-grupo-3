package org.tacsbot.handlers.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.tacsbot.handlers.CommandsHandler;
import org.tacsbot.model.User;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.tacsbot.bot.MyTelegramBot;

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
    public void processResponse(Message message, MyTelegramBot bot) throws IOException, InterruptedException, URISyntaxException {
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
                System.out.println(jsonrequest);
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                            .uri(new URI(System.getenv("RESOURCE_URL") + "/users/login"))
                            .POST(HttpRequest.BodyPublishers.ofString(jsonrequest))
                            .header("Content-Type","application/json")
                            .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println(response.statusCode());
                    if(response.statusCode() == 200){
                        String userString = response.body();
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
