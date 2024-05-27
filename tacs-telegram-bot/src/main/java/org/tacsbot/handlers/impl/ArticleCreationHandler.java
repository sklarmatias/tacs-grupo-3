package org.tacsbot.handlers.impl;

import lombok.Setter;
import org.apache.http.HttpException;
import org.tacsbot.api.article.impl.ArticleApiConnection;
import org.tacsbot.bot.MyTelegramBot;
import org.tacsbot.handlers.CommandsHandler;
import org.tacsbot.helper.ArticleValidatorHelper;
import org.tacsbot.model.Article;
import org.tacsbot.model.CostType;
import org.tacsbot.handlers.CommandsHandler;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ArticleCreationHandler implements CommandsHandler {
    private Long chatId;
    private ArticleCreationStep currentStep;
    private String articleName;
    private Date deadLine;
    private CostType costType;
    private Double cost;
    private Integer maxNumUsers;
    private Integer minNumUsers;
    private String image;
    private String userGets;
    private String userId;

    // Enum to represent the different steps of the article creation process
    private enum ArticleCreationStep {
        REQUEST_NAME,
        REQUEST_DEADLINE,
        REQUEST_COST_TYPE,
        REQUEST_COST,
        REQUEST_USERGETS,
        REQUEST_MIN_USERS,
        REQUEST_MAX_USERS,
        REQUEST_IMAGE, FINALIZAR
    }

    public ArticleCreationHandler(Long userId) {
        this.chatId = userId;
        this.currentStep = ArticleCreationStep.REQUEST_NAME;
    }

    @Override
    public void processResponse(Message message, MyTelegramBot bot) throws IOException, InterruptedException, URISyntaxException {
        String errorMessage = null;
        switch (currentStep) {
            case REQUEST_NAME:
                // Step 1: Request article's name

                errorMessage = ArticleValidatorHelper.validateArticleName(message.getText());
                if (errorMessage == null){
                    articleName = message.getText();

                currentStep = ArticleCreationStep.REQUEST_DEADLINE;
                bot.sendText(chatId, "Por favor ingresa la fecha límite (YYYY-MM-DD):");
                }else {
                    bot.sendText(chatId, errorMessage + "Ingrese un nombre nuevamente...");
                }
                break;
            case REQUEST_DEADLINE:
                // Step 2: Request the article's deadline
                try {
                    deadLine = new SimpleDateFormat("yyyy-MM-dd").parse(message.getText());
                    currentStep = ArticleCreationStep.REQUEST_COST_TYPE;
                    bot.sendText(chatId, "Por favor ingresa el tipo de costo (Total o Per_user):");
                } catch (ParseException e) {
                    bot.sendText(chatId, "Formato de fecha incorrecto. Por favor, ingrese la fecha nuevamente usando el formato YYYY-MM-DD");
                }
                break;
            case REQUEST_COST_TYPE:
                // Step 3: Request the article's cost type
                String tipoCostoString = message.getText().toUpperCase();
                errorMessage = ArticleValidatorHelper.validateCostType(tipoCostoString);
                if (errorMessage == null) {
                    costType = CostType.valueOf(tipoCostoString);
                    currentStep = ArticleCreationStep.REQUEST_COST;
                    bot.sendText(chatId, "Por favor ingresa el costo:");
                }else {
                    bot.sendText(chatId, errorMessage + "Ingrese el tipo de costo nuevamente...");
                }
                break;
            case REQUEST_COST:
                // Step 4: Request the article's cost
                try {
                    cost = Double.parseDouble(message.getText());
                    currentStep = ArticleCreationStep.REQUEST_USERGETS;
                    bot.sendText(chatId, "Por favor ingresa lo que obtiene cada usuario:");
                } catch (NumberFormatException e) {
                    bot.sendText(chatId, "Formato de costo incorrecto. Por favor, ingresa un número válido (xxxx.xx)");
                }
                break;
            case REQUEST_USERGETS:
                errorMessage = ArticleValidatorHelper.validateUserGets(message.getText());
                if (errorMessage == null){
                    userGets = message.getText();

                    currentStep = ArticleCreationStep.REQUEST_MAX_USERS;
                    bot.sendText(chatId, "Por favor ingresa la cantidad máxima de usuarios:");
                }else {
                    bot.sendText(chatId, errorMessage + "Ingrese un texto nuevamente...");
                }
                break;
                // TO DO ingresar link
            case REQUEST_MAX_USERS:
                // Paso 5: Solicitar la cantidad máxima de usuarios del artículo
                try {
                    maxNumUsers = Integer.parseInt(message.getText());
                    currentStep = ArticleCreationStep.REQUEST_MIN_USERS;
                    bot.sendText(chatId, "Por favor ingresa la cantidad mínima de usuarios:");
                } catch (NumberFormatException e) {
                    bot.sendText(chatId, "Formato de cantidad incorrecto. Por favor, ingresa un número válido.");
                }
                break;
            case REQUEST_MIN_USERS:
                // Step 6: Request the article's minimum number of users
                try {
                    minNumUsers = Integer.parseInt(message.getText());
                    errorMessage = ArticleValidatorHelper.validateMinNumUsers(minNumUsers, maxNumUsers);
                    if ( errorMessage == null) {
                        currentStep = ArticleCreationStep.REQUEST_IMAGE;

                        bot.sendText(chatId, "Adjunte la imagen");
                    }else {
                        bot.sendText(chatId, errorMessage);
                    }
                } catch (NumberFormatException e) {
                    bot.sendText(chatId, "Formato de cantidad incorrecto. Por favor, ingresa un número válido.");
                }
                break;
            case REQUEST_IMAGE:
                //todo logica para guardar imagen
                // TODO pedir lo que recibe cada usuario
                // TODO: Asignar el propietario y la fecha de creación al artículo
                image = message.getText();
                this.userId = bot.usersLoginMap.get(chatId);
                try{
                    String articleId = new ArticleApiConnection().createArticle(new Article(
                            null,
                            articleName,
                            image,
                            null,
                            userGets,
                            null,
                            deadLine,
                            null,
                            null,
                            null,
                            null,
                            cost,
                            costType,
                            minNumUsers,
                            maxNumUsers
                    ));
                    bot.sendText(chatId, "Articulo creado de manera exitosa!");
                } catch (HttpException e){
                    // couldnt create, server error
                    bot.sendText(chatId, "No se pudo crear el articulo. Por favor, intentelo de nuevo más tarde.");
                } catch(IllegalArgumentException e){
                    // couldnt create, input error
                    bot.sendText(chatId, "No se pudo crear el articulo: uno o más campos incorrectos. Por favor, intentelo nuevamente.");
                }
                bot.resetUserHandlers(chatId);

        }
    }
}