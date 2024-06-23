package ar.edu.utn.frba.tests.handler;

import org.apache.http.HttpException;
import org.junit.Before;
import org.junit.Test;
import org.tacsbot.api.article.ArticleApi;
import org.tacsbot.api.article.impl.ArticleApiConnection;
import org.tacsbot.api.user.UserApi;
import org.tacsbot.api.user.impl.UserApiConnection;
import org.tacsbot.bot.MyTelegramBot;
import org.tacsbot.cache.CacheService;
import org.tacsbot.cache.impl.RedisService;
import org.tacsbot.handlers.impl.ArticleHandler;
import org.tacsbot.handlers.impl.ArticleType;
import org.tacsbot.handlers.impl.RegisterHandler;
import org.tacsbot.model.Annotation;
import org.tacsbot.model.Article;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ArticleHandlerTest {
    private Message message;
    private ArticleHandler articleHandler;
    private MyTelegramBot bot;
    private ArticleApi api;
    List<Article> articleList;
    List<Annotation> annotationList;
    @Before
    public void mockMessageApiAndBot() throws IOException, HttpException {
        // message
        message = new Message();
        message.setFrom(new User());
        message.setChat(new Chat(123L,"type"));

        api = mock(ArticleApiConnection.class);
        // bot
        bot = mock(MyTelegramBot.class);
        doNothing().when(bot).logInUser(any(),any());
        doNothing().when(bot).sendInteraction(any(), anyString());
        doNothing().when(bot).sendArticleList(any(), any());
        doNothing().when(bot).sendArticle(any(),any());
        doNothing().when(bot).sendAnnotationList(any(),any());
        articleHandler = new ArticleHandler(1234L);
        articleHandler.setArticleApiConnector(api);

        CacheService cacheService = mock(RedisService.class);
        org.tacsbot.model.User user = new org.tacsbot.model.User();
        user.setId("userid");
        user.setName("name");
        doReturn(user).when(cacheService).getUser(any());
        doReturn(cacheService).when(bot).getCacheService();

        articleList = new ArrayList<>();
        Article article = new Article();
        article.setId("articleid");
        articleList.add(article);
        doReturn(articleList).when(api).getAllArticles();
        doReturn(articleList).when(api).getArticlesOf(any());

        annotationList = new ArrayList<>();
        annotationList.add(new Annotation());
        doReturn(annotationList).when(api).viewArticleSubscriptions(any());
        doReturn(true).when(api).suscribeToArticle(any(),anyString());
        doReturn(article).when(api).closeArticle(any(),anyString());

    }
    @Test
    public void testArticleTypeAEmpty() throws HttpException {
        List<Article> articleList = new ArrayList<>();
        doReturn(articleList).when(api).getAllArticles();
        message.setText("A");
        articleHandler.processResponse(message,bot);
        verify(bot).sendInteraction(any(User.class), eq("NO_ARTICLES"));
    }
    @Test
    public void testArticleTypeBEmpty() throws HttpException {
        List<Article> articleList = new ArrayList<>();
        doReturn(articleList).when(api).getArticlesOf(any());
        message.setText("B");
        articleHandler.processResponse(message,bot);
        verify(bot).sendInteraction(any(User.class), eq("NO_ARTICLES"));
    }
    @Test
    public void testArticleTypeWrong() throws HttpException {
        message.setText("C");
        articleHandler.processResponse(message,bot);
        verify(bot).sendInteraction(any(User.class), eq("UNKNOWN_RESPONSE"));
    }
    @Test
    public void testArticleWrongIndex() throws HttpException {
        message.setText("B");
        articleHandler.processResponse(message,bot);
        message.setText("B");
        articleHandler.processResponse(message,bot);
        verify(bot).sendInteraction(any(User.class), eq("UNKNOWN_RESPONSE"));
    }
    @Test
    public void testArticleIndexOutOfBound() throws HttpException {
        message.setText("B");
        articleHandler.processResponse(message,bot);
        message.setText("4");
        articleHandler.processResponse(message,bot);
        verify(bot).sendInteraction(any(User.class), eq("ARTICLE_INVALID_INDEX"));
    }
    @Test
    public void testArticleSubscribeCancel() throws HttpException {
        message.setText("A");
        articleHandler.processResponse(message,bot);
        message.setText("1");
        articleHandler.processResponse(message,bot);
        message.setText("B");
        articleHandler.processResponse(message,bot);
        verify(bot).sendInteraction(any(User.class), eq("CANCELLATION"),eq("name"));
    }

    @Test
    public void testArticleSubscribeWrongInput() throws HttpException {
        message.setText("A");
        articleHandler.processResponse(message,bot);
        message.setText("1");
        articleHandler.processResponse(message,bot);
        message.setText("C");
        articleHandler.processResponse(message,bot);
        verify(bot).sendInteraction(any(User.class), eq("UNKNOWN_RESPONSE"));
    }

    @Test
    public void testArticleOwnArticleWrongInput() throws HttpException {
        message.setText("B");
        articleHandler.processResponse(message,bot);
        message.setText("1");
        articleHandler.processResponse(message,bot);
        message.setText("C");
        articleHandler.processResponse(message,bot);
        verify(bot).sendInteraction(any(User.class), eq("UNKNOWN_RESPONSE"));
    }
    @Test
    public void testArticleSubscribeFail() throws HttpException {
        doReturn(false).when(api).suscribeToArticle(any(),anyString());
        message.setText("A");
        articleHandler.processResponse(message,bot);
        message.setText("1");
        articleHandler.processResponse(message,bot);
        message.setText("A");
        articleHandler.processResponse(message,bot);
        verify(bot).sendInteraction(any(User.class), eq("AVAILABLE_ARTICLES"));
        verify(bot).sendArticleList(any(User.class), eq(articleList));
        verify(bot).sendInteraction(any(User.class), eq("CHOSEN_ARTICLE"), eq(1));
        verify(bot).sendInteraction(any(User.class), eq("SUBSCRIBE_CONFIRMATION"));
        verify(bot).sendInteraction(any(User.class), eq("SUBSCRIBE_FAIL"));
    }
    @Test
    public void testArticleCloseFail() throws HttpException {
        doThrow(IllegalArgumentException.class).when(api).closeArticle(any(),anyString());
        message.setText("B");
        articleHandler.processResponse(message,bot);
        message.setText("1");
        articleHandler.processResponse(message,bot);
        message.setText("B");
        articleHandler.processResponse(message,bot);
        verify(bot).sendInteraction(any(User.class), eq("AVAILABLE_ARTICLES"));
        verify(bot).sendArticleList(any(User.class), eq(articleList));
        verify(bot).sendInteraction(any(User.class), eq("CHOSEN_ARTICLE"), eq(1));
        verify(bot).sendInteraction(any(User.class), eq("CHOOSE_OWN_ARTICLES_ACTION"));
        verify(bot).sendInteraction(any(User.class), eq("ARTICLE_NOT_CLOSED"));
    }
    @Test
    public void testArticleViewSubscriptorsNoSubscriptions() throws HttpException {
        doReturn(new ArrayList<>()).when(api).viewArticleSubscriptions(any());
        message.setText("B");
        articleHandler.processResponse(message,bot);
        message.setText("1");
        articleHandler.processResponse(message,bot);
        message.setText("A");
        articleHandler.processResponse(message,bot);
        verify(bot).sendInteraction(any(User.class), eq("AVAILABLE_ARTICLES"));
        verify(bot).sendArticleList(any(User.class), eq(articleList));
        verify(bot).sendInteraction(any(User.class), eq("CHOSEN_ARTICLE"), eq(1));
        verify(bot).sendInteraction(any(User.class), eq("CHOOSE_OWN_ARTICLES_ACTION"));
        verify(bot).sendInteraction(any(User.class), eq("NO_SUBSCRIPTIONS"));
    }

    @Test
    public void testArticleViewSubscriptorsWrong() throws HttpException {
        doThrow(IllegalArgumentException.class).when(api).viewArticleSubscriptions(any());
        message.setText("B");
        articleHandler.processResponse(message,bot);
        message.setText("1");
        articleHandler.processResponse(message,bot);
        message.setText("A");
        articleHandler.processResponse(message,bot);
        verify(bot).sendInteraction(any(User.class), eq("AVAILABLE_ARTICLES"));
        verify(bot).sendArticleList(any(User.class), eq(articleList));
        verify(bot).sendInteraction(any(User.class), eq("CHOSEN_ARTICLE"), eq(1));
        verify(bot).sendInteraction(any(User.class), eq("CHOOSE_OWN_ARTICLES_ACTION"));
        verify(bot).sendInteraction(any(User.class), eq("UNKNOWN_RESPONSE"));
    }
    @Test
    public void testArticleSubscribeOk() throws HttpException {
        message.setText("A");
        articleHandler.processResponse(message,bot);
        message.setText("1");
        articleHandler.processResponse(message,bot);
        message.setText("A");
        articleHandler.processResponse(message,bot);
        verify(bot).sendInteraction(any(User.class), eq("AVAILABLE_ARTICLES"));
        verify(bot).sendArticleList(any(User.class), eq(articleList));
        verify(bot).sendInteraction(any(User.class), eq("CHOSEN_ARTICLE"), eq(1));
        verify(bot).sendInteraction(any(User.class), eq("SUBSCRIBE_CONFIRMATION"));
        verify(bot).sendInteraction(any(User.class), eq("SUBSCRIBE_SUCCESS"));
    }
    @Test
    public void testArticleCloseOk() throws HttpException {
        message.setText("B");
        articleHandler.processResponse(message,bot);
        message.setText("1");
        articleHandler.processResponse(message,bot);
        message.setText("B");
        articleHandler.processResponse(message,bot);
        verify(bot).sendInteraction(any(User.class), eq("AVAILABLE_ARTICLES"));
        verify(bot).sendArticleList(any(User.class), eq(articleList));
        verify(bot).sendInteraction(any(User.class), eq("CHOSEN_ARTICLE"), eq(1));
        verify(bot).sendInteraction(any(User.class), eq("CHOOSE_OWN_ARTICLES_ACTION"));
        verify(bot).sendInteraction(any(User.class), eq("ARTICLE_CLOSED"));
    }
    @Test
    public void testArticleViewSubscriptorsOk() throws HttpException {
        message.setText("B");
        articleHandler.processResponse(message,bot);
        message.setText("1");
        articleHandler.processResponse(message,bot);
        message.setText("A");
        articleHandler.processResponse(message,bot);
        verify(bot).sendInteraction(any(User.class), eq("AVAILABLE_ARTICLES"));
        verify(bot).sendArticleList(any(User.class), eq(articleList));
        verify(bot).sendInteraction(any(User.class), eq("CHOSEN_ARTICLE"), eq(1));
        verify(bot).sendInteraction(any(User.class), eq("CHOOSE_OWN_ARTICLES_ACTION"));
        verify(bot).sendAnnotationList(any(User.class), eq(annotationList));
    }
    @Test
    public void testShowArticlesWithoutLogin() throws HttpException {
        articleHandler.setArticleType(ArticleType.TODOS);
        articleHandler.processResponse(message,bot);
        verify(bot).sendInteraction(any(User.class), eq("AVAILABLE_ARTICLES"));
        verify(bot).sendArticleList(any(User.class), eq(articleList));
    }
}
