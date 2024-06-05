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
    private ArticleCreationStep currentStep;

    private final Article article;

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
        this.currentStep = ArticleCreationStep.REQUEST_NAME;
        this.articleApi = new ArticleApiConnection();
        this.article = new Article();
    }

    private void createArticle(Message message, Article article, MyTelegramBot bot) throws HttpException, IOException {
        try{
            articleApi.createArticle(article);
            bot.sendInteraction(message.getFrom(), "ARTICLE_CREATED");
        } catch(IllegalArgumentException e){
            bot.sendInteraction(message.getFrom(), "ARTICLE_NOT_CREATED");
        }
    }

    @Override
    public void processResponse(Message message, MyTelegramBot bot) throws HttpException, IOException{
        Long chatId = message.getChatId();
        String errorMessage;
        switch (currentStep) {
            case REQUEST_NAME:
                // Step 1: Request article's name

                errorMessage = ArticleValidatorHelper.validateArticleName(message.getText());
                if (errorMessage == null){
                    article.setName(message.getText());
                currentStep = ArticleCreationStep.REQUEST_DEADLINE;
                bot.sendInteraction(message.getFrom(), "ARTICLE_DEADLINE");
                }else {
                    bot.sendText(chatId, errorMessage + "Ingrese un nombre nuevamente...");
                }
                break;
            case REQUEST_DEADLINE:
                // Step 2: Request the article's deadline
                try {
                    Date enteredDeadline = new SimpleDateFormat("yyyy-MM-dd").parse(message.getText());
                    if (enteredDeadline.before(new Date())){
                        bot.sendInteraction(message.getFrom(), "ARTICLE_INVALID_DEADLINE");
                        return;
                    }
                    article.setDeadline(enteredDeadline);
                    currentStep = ArticleCreationStep.REQUEST_COST_TYPE;
                    bot.sendInteraction(message.getFrom(), "ARTICLE_COST_TYPE");
                } catch (ParseException e) {
                    bot.sendInteraction(message.getFrom(), "UNKNOWN_RESPONSE");
                    bot.sendInteraction(message.getFrom(), "ARTICLE_DEADLINE");
                }
                break;
            case REQUEST_COST_TYPE:
                // Step 3: Request the article's cost type
                String tipoCostoString = message.getText().toUpperCase();
                if (tipoCostoString.equals("A"))
                    article.setCostType(CostType.TOTAL);
                else if (tipoCostoString.equals("B"))
                    article.setCostType(CostType.PER_USER);
                else{
                    bot.sendInteraction(message.getFrom(), "UNKNOWN_RESPONSE");
                    bot.sendInteraction(message.getFrom(), "ARTICLE_COST_TYPE");
                    return;
                }
                bot.sendInteraction(message.getFrom(), "ARTICLE_COST");
                currentStep = ArticleCreationStep.REQUEST_COST;
                break;
            case REQUEST_COST:
                // Step 4: Request the article's cost
                try {
                    article.setCost(Double.parseDouble(message.getText()));
                    currentStep = ArticleCreationStep.REQUEST_USERGETS;
                    bot.sendInteraction(message.getFrom(), "ARTICLE_USER_GETS");
                } catch (NumberFormatException e) {
                    bot.sendInteraction(message.getFrom(), "UNKNOWN_RESPONSE");
                    bot.sendInteraction(message.getFrom(), "ARTICLE_COST");
                }
                break;
            case REQUEST_USERGETS:
                errorMessage = ArticleValidatorHelper.validateUserGets(message.getText());
                if (errorMessage == null){
                    article.setUserGets(message.getText());
                    currentStep = ArticleCreationStep.REQUEST_MAX_USERS;
                    bot.sendInteraction(message.getFrom(), "ARTICLE_USERS_MAX");
                }else {
                    bot.sendText(chatId, errorMessage + "Ingrese un texto nuevamente...");
                }
                break;
                // TO DO ingresar link
            case REQUEST_MAX_USERS:
                // Paso 5: Solicitar la cantidad máxima de usuarios del artículo
                try {
                    article.setUsersMax(Integer.parseInt(message.getText()));
                    currentStep = ArticleCreationStep.REQUEST_MIN_USERS;
                    bot.sendInteraction(message.getFrom(), "ARTICLE_USERS_MIN");
                } catch (NumberFormatException e) {
                    bot.sendInteraction(message.getFrom(), "UNKNOWN_RESPONSE");
                    bot.sendInteraction(message.getFrom(), "ARTICLE_USERS_MAX");
                }
                break;
            case REQUEST_MIN_USERS:
                // Step 6: Request the article's minimum number of users
                try {
                    int minNumUsers = Integer.parseInt(message.getText());
                    if (minNumUsers > article.getUsersMax()) {
                        bot.sendInteraction(message.getFrom(), "ARTICLE_INVALID_USERS_MIN", article.getUsersMax());
                        bot.sendInteraction(message.getFrom(), "ARTICLE_USERS_MIN");
                    } else if (minNumUsers < 0) {
                        throw new NumberFormatException();
                    } else{
                    article.setUsersMin(minNumUsers);
                        currentStep = ArticleCreationStep.REQUEST_LINK;
                        bot.sendInteraction(message.getFrom(), "ARTICLE_LINK");
                    }
                } catch (NumberFormatException e) {
                    bot.sendInteraction(message.getFrom(), "UNKNOWN_RESPONSE");
                    bot.sendInteraction(message.getFrom(), "ARTICLE_USERS_MIN");
                }
                break;
            case REQUEST_LINK:
                article.setLink(message.getText());
                currentStep = ArticleCreationStep.REQUEST_IMAGE;
                bot.sendInteraction(message.getFrom(), "ARTICLE_IMAGE");
                break;
            case REQUEST_IMAGE:
                article.setImage(message.getText());
                article.setOwner(bot.usersLoginMap.get(chatId));
                createArticle(message, article, bot);
                bot.resetUserHandlers(chatId);

        }
    }
}