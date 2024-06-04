package org.tacsbot.handlers.impl;

import org.apache.http.HttpException;
import org.tacsbot.api.article.ArticleApi;
import org.tacsbot.api.article.impl.ArticleApiConnection;
import org.tacsbot.bot.MyTelegramBot;
import org.tacsbot.handlers.CommandsHandler;
import org.tacsbot.helper.ArticleValidatorHelper;
import org.tacsbot.model.Article;
import org.tacsbot.model.CostType;
import org.telegram.telegrambots.meta.api.objects.Message;
import java.io.IOException;
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
    private String link;
    private String userGets;
    private String userId;

    private ArticleApi articleApi;


    // Enum to represent the different steps of the article creation process
    private enum ArticleCreationStep {
        REQUEST_NAME,
        REQUEST_DEADLINE,
        REQUEST_COST_TYPE,
        REQUEST_COST,
        REQUEST_USERGETS,
        REQUEST_MIN_USERS,
        REQUEST_MAX_USERS,
        REQUEST_LINK,
        REQUEST_IMAGE
    }

    public ArticleCreationHandler(Long userId) {
        this.chatId = userId;
        this.currentStep = ArticleCreationStep.REQUEST_NAME;
        this.articleApi = new ArticleApiConnection();
    }

    private void createArticle(Article article, MyTelegramBot bot) throws HttpException, IOException {
        try{
            articleApi.createArticle(article);
            bot.sendText(chatId, "Artículo creado de manera exitosa!");
        } catch(IllegalArgumentException e){
            // couldnt create, input error
            bot.sendText(chatId, "No se pudo crear el articulo: uno o más campos incorrectos. Por favor, intentelo nuevamente.");
        }
    }

    @Override
    public void processResponse(Message message, MyTelegramBot bot) throws HttpException, IOException{
        String errorMessage;
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
                    Date enteredDeadline = new SimpleDateFormat("yyyy-MM-dd").parse(message.getText());
                    if (enteredDeadline.before(new Date())){
                        bot.sendText(chatId, "La deadline debe estar en el futuro. Por favor, ingrese la fecha nuevamente:");
                        return;
                    }
                    deadLine = enteredDeadline;
                    currentStep = ArticleCreationStep.REQUEST_COST_TYPE;
                    bot.sendText(chatId, "Por favor ingresa el tipo de costo:\nA) TOTAL\nB) POR USUARIO");
                } catch (ParseException e) {
                    bot.sendText(chatId, "Formato de fecha incorrecto. Por favor, ingrese la fecha nuevamente usando el formato YYYY-MM-DD");
                }
                break;
            case REQUEST_COST_TYPE:
                // Step 3: Request the article's cost type
                String tipoCostoString = message.getText().toUpperCase();
                if (tipoCostoString.equals("A"))
                    costType = CostType.TOTAL;
                else if (tipoCostoString.equals("B"))
                    costType = CostType.PER_USER;
                else{
                    bot.sendInteraction(message.getFrom(), "UNKNOWN_RESPONSE");
                    return;
                }
                bot.sendText(chatId, "Por favor ingresa el costo:");
                currentStep = ArticleCreationStep.REQUEST_COST;
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
                        currentStep = ArticleCreationStep.REQUEST_LINK;

                        bot.sendText(chatId, "Adjunte el link");
                    }else {
                        bot.sendText(chatId, errorMessage);
                    }
                } catch (NumberFormatException e) {
                    bot.sendText(chatId, "Formato de cantidad incorrecto. Por favor, ingresa un número válido.");
                }
                break;
            case REQUEST_LINK:
                link = message.getText();
                currentStep = ArticleCreationStep.REQUEST_IMAGE;
                bot.sendText(chatId, "Adjunte la imagen");
                break;
            case REQUEST_IMAGE:
                image = message.getText();
                this.userId = bot.usersLoginMap.get(chatId);
                createArticle(new Article(
                            null,
                            articleName,
                            image,
                            link,
                            userGets,
                            null,
                            deadLine,
                            userId,
                            null,
                            null,
                            null,
                            cost,
                            costType,
                            minNumUsers,
                            maxNumUsers
                    ), bot);
                bot.resetUserHandlers(chatId);

        }
    }
}