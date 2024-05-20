package org.tacsbot.handlers;

import jakarta.ws.rs.core.Response;
import org.apache.cxf.jaxrs.client.WebClient;
import org.tacsbot.MyTelegramBot;
import org.telegram.telegrambots.meta.api.objects.Message;

public class ArticleHandler implements CommandsHandler {
    private Long chatId;
    private CurrentStep currentStep;
    private int articleId;
    private ArticleType articleType;
    private String action;
    private String user;
    @Override
    public void processResponse(Message message, MyTelegramBot bot) {
        WebClient client;
        Response response;
        String url = System.getenv("RESOURCE_URL") + "/articles";
        user = bot.usersLoginMap.get(chatId);
        switch (currentStep) {
            case CHOOSE_ARTICLE_TYPE:
                articleType = ArticleType.valueOf(message.getText());
                switch (articleType){
                    case TODOS:
                        action = "SUSCRIBIRSE";
                        break;
                    case PROPIOS:
                        action = "VER_SUSCRIPTOS, CERRAR";
                        url += "/user/" + user.toString();
                        break;
                }
                client = WebClient.create(url);
                response = client.accept("application/json").get();
                bot.sendText(chatId, "Estos son los articulos disponibles");
                bot.sendText(chatId,bot.parseJson(response.readEntity(String.class)));
                currentStep = CurrentStep.CHOOSE_ARTICLE;
                break;
            case CHOOSE_ARTICLE:

                articleId = Integer.parseInt(message.getText());
                bot.sendText(chatId, "Elegir la accion. " + action);
                currentStep = CurrentStep.CHOOSE_ACTION;
                break;
            case CHOOSE_ACTION:
                String action = message.getText();
                System.out.println(action);
                switch (action){
                    case "SUSCRIBIRSE":
                        url +=  "/"+ articleId + "/users/" + user.toString();
                        client = WebClient.create(url);
                        response = client.type("application/json").post("");
                        System.out.println(response.getStatus());
                        if(response.getStatus() == 204){
                            bot.sendText(chatId, "Se ha suscripto correctamente.");
                        }
                        else{
                            bot.sendText(chatId, "Ingresar un articulo valido.");
                        }
                        break;
                    case "CERRAR":
                        url +=  "/"+ articleId + "/close";
                        client = WebClient.create(url);
                        response = client.type("application/json").invoke("PATCH", "");
                        if(response.getStatus() == 200){
                            bot.sendText(chatId, "Se ha cerrado correctamente.");
                        }
                        else{
                            bot.sendText(chatId, "No se pudo cerrar.");
                        }
                        break;
                    case "VER_SUSCRIPTOS":
                        url += "/"+ articleId + "/users";
                        client = WebClient.create(url);
                        response = client.accept("application/json").get();
                        String responseMessage = "Estos son los usuarios anotados al articulo:" + response.readEntity(String.class);
                        bot.sendText(chatId, responseMessage);
                        System.out.printf("Artículos obtenidos por el usuario %d", chatId);
                        break;
                }
        }
    }

    private enum CurrentStep {
        CHOOSE_ARTICLE_TYPE,
        CHOOSE_ARTICLE,
        CHOOSE_ACTION
    }
    private enum ArticleType {
        TODOS,
        PROPIOS
    }
    public ArticleHandler(Long userId) {
        this.chatId = userId;
        this.currentStep = CurrentStep.CHOOSE_ARTICLE_TYPE;
    }
}
