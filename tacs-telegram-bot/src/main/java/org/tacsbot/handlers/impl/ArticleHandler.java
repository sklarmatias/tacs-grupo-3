package org.tacsbot.handlers.impl;

import lombok.Setter;
import org.apache.http.HttpException;
import org.tacsbot.api.article.ArticleApi;
import org.tacsbot.api.article.impl.ArticleApiConnection;
import org.tacsbot.bot.MyTelegramBot;
import org.tacsbot.handlers.CommandsHandler;
import org.tacsbot.model.Annotation;
import org.tacsbot.model.Article;
import org.telegram.telegrambots.meta.api.objects.Message;
import java.util.List;

@Setter
public class ArticleHandler implements CommandsHandler {
    private final Long chatId;
    private CurrentStep currentStep;
    private String articleId;
    private ArticleType articleType;
    private Integer selectedArticleIndex;
    private List<Article> articleList;
    private String action;
    private String user;
    private ArticleApi articleApiConnector;

    public ArticleHandler(Long userId) {
        this.chatId = userId;
        this.currentStep = CurrentStep.CHOOSE_ARTICLE_TYPE;
        articleApiConnector = new ArticleApiConnection();
    }

    private String parseArticlesToMessage(List<Article> articles) {
        String articleString = "";
        int indice = 1;
        for (Article article : articles) {
            articleString += String.format("*INDICE:* %d\n%s", indice, article.getDetailedString());
            indice += 1;
        }
        return articleString;
    }

    private void subscribe(Message message, String userId, MyTelegramBot bot) throws HttpException {
        Article article = new Article();
        article.setId(articleId);
        if (articleApiConnector.suscribeToArticle(article, userId))
            bot.sendInteraction(message.getFrom(), "SUBSCRIBE_SUCCESS");
        else bot.sendInteraction(message.getFrom(), "SUBSCRIBE_FAIL");
    }

    private void closeArticle(Message message, String userId, MyTelegramBot bot) throws HttpException {
        Article article = new Article();
        article.setId(articleId);
        Article closedArticle = articleApiConnector.closeArticle(article, userId);
        bot.sendInteraction(message.getFrom(), "ARTICLE_CLOSED");
        bot.sendText(chatId, closedArticle.getDetailedString());
    }

    private void getArticles(List<Article> articles, MyTelegramBot bot) {
        this.articleList = articles;
        if (articles.isEmpty())
            bot.sendText(chatId, "Todavia no hay articulos.");
        else {
            bot.sendText(chatId, "Estos son los artículos disponibles:");
            bot.sendText(chatId, parseArticlesToMessage(articles), true);
        }
    }

    private void getAllArticles(MyTelegramBot bot) throws HttpException {
        List<Article> articles = articleApiConnector.getAllArticles();
        getArticles(articles, bot);
    }

    private void getArticlesOf(String userId, MyTelegramBot bot) throws HttpException {
        List<Article> articles = articleApiConnector.getArticlesOf(userId);
        getArticles(articles, bot);
    }

    private boolean validateSelectedIndex(Integer selectedIndex) {
        return selectedIndex >= 0 && selectedIndex < articleList.size();
    }

    private String createSubscriptionsText(List<Annotation> annotations){
        String s = "";
        for (Annotation annotation: annotations)
            s += annotation.toString() + "\n";
        return s;
    }

    private void getSubscriptions(String articleId, MyTelegramBot bot) throws HttpException {
        Article article = new Article();
        article.setId(articleId);
        try{
            List<Annotation> subscriptions = articleApiConnector.viewArticleSubscriptions(article);
            bot.sendText(chatId, createSubscriptionsText(subscriptions));
        } catch (IllegalArgumentException e){
            bot.sendInternalErrorMsg(chatId, e);
        }
    }

    @Override
    public void processResponse(Message message, MyTelegramBot bot) throws HttpException {
        switch (currentStep) {
            case CHOOSE_ARTICLE_TYPE:
                if (articleType == null)
                    articleType = ArticleType.valueOf(message.getText().toUpperCase());
                switch (articleType) {
                    case TODOS:
                        getAllArticles(bot);
                        if (bot.usersLoginMap.containsKey(chatId)){
                            action = "SUSCRIBIRSE";
                            currentStep = CurrentStep.CHOOSE_ARTICLE;
                            bot.sendText(chatId, "Elegir el articulo indicando su numero de indice");
                        }
                        return;
                    case PROPIOS:
                        action = "VER_SUSCRIPTOS, CERRAR";
                        user = bot.usersLoginMap.get(chatId);
                        getArticlesOf(user, bot);
                        currentStep = CurrentStep.CHOOSE_ARTICLE;
                        bot.sendText(chatId, "Elegir el articulo indicando su numero de indice");
                        return;
                }
            case CHOOSE_ARTICLE:

                int selectedIndex = Integer.parseInt(message.getText()) - 1;
                if (!validateSelectedIndex(selectedIndex)) {
                    System.out.printf("[INFO] wrong article index %d <0.\n", selectedIndex);
                    bot.sendText(chatId, "Ingresaste un índice incorrecto. Por favor, volvé a intentarlo.");
                    currentStep = CurrentStep.CHOOSE_ARTICLE;
                    return;
                }
                selectedArticleIndex = selectedIndex;
                articleId = articleList.get(selectedArticleIndex).getId();
                bot.sendText(chatId, "Elegiste el artículo " + articleList.get(selectedArticleIndex).getName() + ".");
                bot.sendText(chatId, "Elegir la accion: " + action);
                currentStep = CurrentStep.CHOOSE_ACTION;
                break;
            case CHOOSE_ACTION:
            String action = message.getText();
            System.out.println(action);
            switch (action) {
                case "SUSCRIBIRSE":
                    user = bot.usersLoginMap.get(chatId);
                    subscribe(message, user, bot);
                    break;
                case "CERRAR":
                    user = bot.usersLoginMap.get(chatId);
                    closeArticle(message, user, bot);
                    break;
                case "VER_SUSCRIPTOS":
                    user = bot.usersLoginMap.get(chatId);
                    getSubscriptions(articleId, bot);
                    break;
            }
        }
    }

}

