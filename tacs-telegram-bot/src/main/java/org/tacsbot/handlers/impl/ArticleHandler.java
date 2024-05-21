package org.tacsbot.handlers.impl;

import jakarta.ws.rs.core.Response;
import org.apache.cxf.jaxrs.client.WebClient;
import org.tacsbot.bot.MyTelegramBot;
import org.tacsbot.handlers.CommandHandler;
import org.telegram.telegrambots.meta.api.objects.Message;

public class ArticleHandler implements CommandHandler {
    private Long chatId;
    private CurrentStep currentStep;
    private String articleId;
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
                        url += "/user/" + user;
                        break;
                }
                client = WebClient.create(url);
                response = client.header("user",user).accept("application/json").get();
                System.out.println(response.getStatus());
                String articleList = bot.parseJson(response.readEntity(String.class));
                if (response.getStatus() == 200) {
                    System.out.println("articulos obtenidos");
                    System.out.println(articleList);
                    bot.sendText(chatId, "articulos obtenidos con exito");
                    bot.sendText(chatId, articleList);
                }
                else{
                    System.out.println("articulos no obtenidos");
                    System.out.println(response.getStatus());
                    System.out.println(response.readEntity(String.class));
                    System.out.println("articulos obtenidos");
                    System.out.println(articleList);
                    bot.sendText(chatId, "articulos no obtenidos");
                }
                bot.sendText(chatId, "Estos son los articulos disponibles");
                bot.sendText(chatId,articleList);
                currentStep = CurrentStep.CHOOSE_ARTICLE;
                bot.sendText(chatId, "Elegir el articulo indicando su id");
                break;
            case CHOOSE_ARTICLE:

                articleId = message.getText();
                bot.sendText(chatId, "Elegir la accion. " + action);
                currentStep = CurrentStep.CHOOSE_ACTION;
                break;
            case CHOOSE_ACTION:
                String action = message.getText();
                System.out.println(action);
                switch (action){
                    case "SUSCRIBIRSE":
                        url +=  "/"+ articleId + "/users/" + user;
                        client = WebClient.create(url);
                        response = client.header("user",user).type("application/json").post("");
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
                        response = client.header("user",user).type("application/json").invoke("PATCH", "");
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
