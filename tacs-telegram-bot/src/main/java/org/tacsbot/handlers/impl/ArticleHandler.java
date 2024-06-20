package org.tacsbot.handlers.impl;

import lombok.NonNull;
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
    private String user;
    private ArticleApi articleApiConnector;

    public ArticleHandler(Long userId) {
        this.chatId = userId;
        this.currentStep = CurrentStep.CHOOSE_ARTICLE_TYPE;
        articleApiConnector = new ArticleApiConnection();
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
        bot.sendArticle(message.getFrom(), closedArticle);
    }

    private void getArticles(Message message, List<Article> articles, MyTelegramBot bot) {
        this.articleList = articles;
        if (articles.isEmpty())
            bot.sendInteraction(message.getFrom(), "NO_ARTICLES");
        else {
            bot.sendInteraction(message.getFrom(), "AVAILABLE_ARTICLES");
            bot.sendArticleList(message.getFrom(),articles);
        }
    }

    private void getAllArticles(Message message, MyTelegramBot bot) throws HttpException {
        List<Article> articles = articleApiConnector.getAllArticles();
        getArticles(message, articles, bot);
    }

    private void getArticlesOf(Message message, String userId, MyTelegramBot bot) throws HttpException {
        List<Article> articles = articleApiConnector.getArticlesOf(userId);
        getArticles(message, articles, bot);
    }

    private boolean validateSelectedIndex(Integer selectedIndex) {
        return selectedIndex >= 0 && selectedIndex < articleList.size();
    }

    private void getSubscriptions(Message message, @NonNull String articleId, MyTelegramBot bot) throws HttpException {
        Article article = new Article();
        article.setId(articleId);
        try{
            List<Annotation> subscriptions = articleApiConnector.viewArticleSubscriptions(article);
            if (subscriptions.isEmpty())
                bot.sendInteraction(message.getFrom(), "NO_SUBSCRIPTIONS");
            else
                bot.sendAnnotationList(message.getFrom(), subscriptions);
        } catch (IllegalArgumentException e){
            bot.sendInternalErrorMsg(message.getFrom(), e);
        }
    }

    @Override
    public void processResponse(Message message, MyTelegramBot bot) throws HttpException {
        switch (currentStep) {
            case CHOOSE_ARTICLE_TYPE:
                if (articleType == null){
                    if (message.getText().equalsIgnoreCase("A")){
                        articleType = ArticleType.TODOS;
                    } else if (message.getText().equalsIgnoreCase("B"))
                        articleType = ArticleType.PROPIOS;
                    else{
                        bot.sendInteraction(message.getFrom(), "UNKNOWN_RESPONSE");
                        break;
                    }
                }
                switch (articleType) {
                    case TODOS:
                        getAllArticles(message, bot);
                        if(bot.getCacheService().getUser(chatId) != null && !articleList.isEmpty()) {
                            currentStep = CurrentStep.CHOOSE_ARTICLE;
                            bot.sendInteraction(message.getFrom(), "CHOOSE_ARTICLE_INDEX");
                        }
                        return;
                    case PROPIOS:
                        user = bot.getCacheService().getUser(chatId).getId();
                        getArticlesOf(message, user, bot);
                        if (!articleList.isEmpty()){
                            currentStep = CurrentStep.CHOOSE_ARTICLE;
                            bot.sendInteraction(message.getFrom(), "CHOOSE_ARTICLE_INDEX");
                        }
                        return;
                }
            case CHOOSE_ARTICLE:

                int selectedIndex = Integer.parseInt(message.getText()) - 1;
                if (!validateSelectedIndex(selectedIndex)) {
                    bot.sendInteraction(message.getFrom(), "ARTICLE_INVALID_INDEX");
                    currentStep = CurrentStep.CHOOSE_ARTICLE;
                    return;
                }
                selectedArticleIndex = selectedIndex;
                articleId = articleList.get(selectedArticleIndex).getId();
                bot.sendInteraction(message.getFrom(), "CHOSEN_ARTICLE", selectedIndex + 1);
                if (articleType == ArticleType.TODOS)
                    bot.sendInteraction(message.getFrom(), "SUBSCRIBE_CONFIRMATION");
                else bot.sendInteraction(message.getFrom(), "CHOOSE_OWN_ARTICLES_ACTION");
                currentStep = CurrentStep.CHOOSE_ACTION;
                break;
            case CHOOSE_ACTION:
                String action = message.getText().toUpperCase();
                if (articleType == ArticleType.TODOS){
                    if (action.equals("A")){
                        user = bot.getCacheService().getUser(chatId).getId();
                        subscribe(message, user, bot);
                    } else if (action.equals("B")) {
                        bot.sendInteraction(message.getFrom(), "CANCELLATION", bot.getCacheService().getUser(chatId).getName());
                    } else{
                        bot.sendInteraction(message.getFrom(), "UNKNOWN_RESPONSE");
                    }
                } else if (articleType == ArticleType.PROPIOS) {
                    user = bot.getCacheService().getUser(chatId).getId();
                    switch (action) {
                        case "A":
                            getSubscriptions(message, articleId, bot);
                            break;
                        case "B":
                            closeArticle(message, user, bot);
                            break;
                    }
                }
                else {
                    throw new IllegalStateException("Wrong articleType!");
                }
                break;
        }
    }

}

