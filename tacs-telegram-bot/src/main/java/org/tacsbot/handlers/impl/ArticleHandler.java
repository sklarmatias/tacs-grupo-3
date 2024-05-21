package org.tacsbot.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.Response;
import org.apache.cxf.jaxrs.client.WebClient;
import org.tacsbot.MyTelegramBot;
import org.tacsbot.clases.Article;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class ArticleHandler implements CommandsHandler {
    private Long chatId;
    private CurrentStep currentStep;
    private String articleId;
    private ArticleType articleType;
    private Integer selectedArticleIndex;
    private List<Article> articles = new ArrayList<>();
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
                articleType = ArticleType.valueOf(message.getText().toUpperCase());
                switch (articleType){
                    case TODOS:
                        action = "SUSCRIBIRSE";
                        client = WebClient.create(url);
                        response = client.accept("application/json").get();
                        break;
                    case PROPIOS:
                        action = "VER_SUSCRIPTOS, CERRAR";
                        client = WebClient.create(url);
                        response = client
                                .header("user",user)
                                .accept("application/json").get();
                        break;
                }
                client = WebClient.create(url);
                if(articleType.equals(ArticleType.valueOf("TODOS"))){
                   response = client.accept("application/json").get();
               }else{
                    response = client
                            .header("user",user)
                            .accept("application/json").get();
               }
                System.out.println(response.getStatus());
                String responseJson = response.readEntity(String.class);
                String articleList = bot.parseJson(responseJson);
                ObjectMapper mapper = new ObjectMapper();
                try {
                    System.out.println("Intento mapear json ListArticle");
                    this.articles = mapper.readValue(responseJson, new TypeReference<List<Article>>(){});
                    if (!articles.isEmpty()) {
                        System.out.println("Id del primer articulo: " + articles.get(0).id);
                    } else {
                        System.out.println("La lista de artículos está vacía.");
                    }
                } catch (JsonProcessingException e) {
                    System.out.println("Error al procesar el JSON: " + e.getMessage());
                } catch (NoSuchElementException e) {
                    System.out.println("No se encontraron artículos en la lista: " + e.getMessage());
                }

                if (response.getStatus() == 200) {
                    System.out.println(articleList);
                    bot.sendText(chatId, "Estos son los articulos vigentes:");
                    bot.sendText(chatId, articleList);
                    currentStep = CurrentStep.CHOOSE_ARTICLE;
                    bot.sendText(chatId, "Elegir el articulo indicando su numero de indice");
                }
                else{
                    System.out.println("articulos no obtenidos");
                    System.out.println(response.getStatus());
                    System.out.println(response.readEntity(String.class));
                    System.out.println(articleList);
                    bot.sendText(chatId, "Error. No se pudieron obtener los articulos");
                }

                break;
            case CHOOSE_ARTICLE:

                selectedArticleIndex = Integer.valueOf(message.getText());
                articleId = articles.get(selectedArticleIndex).id;

                bot.sendText(chatId, "Elegir la accion. " + action);
                currentStep = CurrentStep.CHOOSE_ACTION;
                break;
            case CHOOSE_ACTION:
                String action = message.getText();
                System.out.println(action);
                switch (action){
                    case "SUSCRIBIRSE":
                        url +=  "/"+ articleId + "/users/";
                        System.out.println(url);
                        client = WebClient.create(url);
                        response = client.header("user",user).type("application/json").post("");
                        System.out.println(response.getStatus());
                        if(response.getStatus() == 200){
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
