package org.tacsbot.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.Response;
import org.tacsbot.clases.User;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.apache.cxf.jaxrs.client.WebClient;
import org.tacsbot.BotPrincipal;

public class LoginHandler implements CommandsHandler {
    private Long chatId;
    private PasoLogin pasoActual;
    private String email;
    private String pass;

    @Override
    public void procesarRespuesta(Message respuesta, BotPrincipal bot) {
        switch (pasoActual) {
            case SOLICITAR_MAIL:
                // Paso 1: Solicitar el nombre del artículo
                email = respuesta.getText();
                pasoActual = PasoLogin.SOLICITAR_CLAVE;
                bot.sendText(chatId, "Por favor ingresa la clave:");
                break;
            case SOLICITAR_CLAVE:
                pass = respuesta.getText();
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

    private enum PasoLogin {
        SOLICITAR_MAIL,
        SOLICITAR_CLAVE
    }

    public LoginHandler(Long userId) {
        this.chatId = userId;
        this.pasoActual = PasoLogin.SOLICITAR_MAIL;
    }
}
