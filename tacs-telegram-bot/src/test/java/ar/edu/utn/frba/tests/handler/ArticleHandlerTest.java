package ar.edu.utn.frba.tests.handler;

import org.apache.http.HttpException;
import org.junit.Before;
import org.junit.Test;
import org.tacsbot.api.article.impl.ArticleApiConnection;
import org.tacsbot.api.article.impl.ArticleHttpConnector;
import org.tacsbot.bot.MyTelegramBot;
import org.tacsbot.cache.CacheService;
import org.tacsbot.cache.impl.RedisService;
import org.tacsbot.handlers.impl.ArticleHandler;
import org.tacsbot.handlers.impl.ArticleType;
import org.tacsbot.model.Annotation;
import org.tacsbot.model.Article;
import org.tacsbot.parser.annotation.impl.AnnotationJSONParser;
import org.tacsbot.parser.article.impl.ArticleJSONParser;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ArticleHandlerTest {
    private Message message;
    private ArticleHandler articleHandler;
    private MyTelegramBot bot;
    private ArticleApiConnection api;
    List<Article> articleList;
    List<Annotation> annotationList;
    private ArticleHttpConnector connector;
    private ArticleJSONParser articleJSONParser = new ArticleJSONParser();
    private AnnotationJSONParser annotationJSONParser = new AnnotationJSONParser();
    @Before
    public void mockMessageApiAndBot() throws IOException, HttpException, URISyntaxException, InterruptedException {
        // message
        message = new Message();
        message.setFrom(new User());
        message.setChat(new Chat(123L,"type"));

        api = new ArticleApiConnection();
        connector = mock(ArticleHttpConnector.class);
        api.setArticleHttpConnector(connector);
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
        HttpResponse response1 =  mock(HttpResponse.class);
        doReturn(200).when(response1).statusCode();
        doReturn(articleJSONParser.parseArticleListToJSON(articleList)).when(response1).body();
        doReturn(response1).when(connector).getArticles(any());

        annotationList = new ArrayList<>();
        annotationList.add(new Annotation());
        HttpResponse response2 =  mock(HttpResponse.class);
        doReturn(200).when(response2).statusCode();
        doReturn(annotationJSONParser.parseAnnotationsToJSON(annotationList)).when(response2).body();
        doReturn(response2).when(connector).getSubscriptions(any());

        HttpResponse response3 =  mock(HttpResponse.class);
        doReturn(200).when(response3).statusCode();
        doReturn(articleJSONParser.parseArticleToJSON(article)).when(response3).body();
        doReturn(response3).when(connector).closeArticle(any(),any());

        HttpResponse response4 =  mock(HttpResponse.class);
        doReturn(200).when(response4).statusCode();
        doReturn(response4).when(connector).suscribeToArticle(any(),any());


    }
    @Test
    public void testArticleTypeAEmpty() throws HttpException, IOException, URISyntaxException, InterruptedException {
        List<Article> articleList = new ArrayList<>();
        HttpResponse response1 =  mock(HttpResponse.class);
        doReturn(200).when(response1).statusCode();
        doReturn(articleJSONParser.parseArticleListToJSON(articleList)).when(response1).body();
        doReturn(response1).when(connector).getArticles(any());
        message.setText("A");
        articleHandler.processResponse(message,bot);
        verify(bot).sendInteraction(any(User.class), eq("NO_ARTICLES"));
    }
    @Test
    public void testArticleTypeBEmpty() throws HttpException, IOException, URISyntaxException, InterruptedException {
        List<Article> articleList = new ArrayList<>();
        HttpResponse response1 =  mock(HttpResponse.class);
        doReturn(200).when(response1).statusCode();
        doReturn(articleJSONParser.parseArticleListToJSON(articleList)).when(response1).body();
        doReturn(response1).when(connector).getArticles(any());
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
    public void testArticleSubscribeFail() throws HttpException, URISyntaxException, IOException, InterruptedException {
        HttpResponse response =  mock(HttpResponse.class);
        doReturn(400).when(response).statusCode();
        doReturn(response).when(connector).suscribeToArticle(any(),any());
        message.setText("A");
        articleHandler.processResponse(message,bot);
        message.setText("1");
        articleHandler.processResponse(message,bot);
        message.setText("A");
        articleHandler.processResponse(message,bot);
        verify(bot).sendInteraction(any(User.class), eq("AVAILABLE_ARTICLES"));
        verify(bot).sendArticleList(any(User.class), any(List.class));
        verify(bot).sendInteraction(any(User.class), eq("CHOSEN_ARTICLE"), eq(1));
        verify(bot).sendInteraction(any(User.class), eq("SUBSCRIBE_CONFIRMATION"));
        verify(bot).sendInteraction(any(User.class), eq("SUBSCRIBE_FAIL"));
    }
    @Test
    public void testArticleCloseFail() throws HttpException, URISyntaxException, IOException, InterruptedException {
        HttpResponse response =  mock(HttpResponse.class);
        doReturn(400).when(response).statusCode();
        doReturn(response).when(connector).closeArticle(any(),any());
        message.setText("B");
        articleHandler.processResponse(message,bot);
        message.setText("1");
        articleHandler.processResponse(message,bot);
        message.setText("B");
        articleHandler.processResponse(message,bot);
        verify(bot).sendInteraction(any(User.class), eq("AVAILABLE_ARTICLES"));
        verify(bot).sendArticleList(any(User.class), any(List.class));
        verify(bot).sendInteraction(any(User.class), eq("CHOSEN_ARTICLE"), eq(1));
        verify(bot).sendInteraction(any(User.class), eq("CHOOSE_OWN_ARTICLES_ACTION"));
        verify(bot).sendInteraction(any(User.class), eq("ARTICLE_NOT_CLOSED"));
    }
    @Test
    public void testArticleViewSubscriptorsNoSubscriptions() throws HttpException, IOException, URISyntaxException, InterruptedException {
        HttpResponse response =  mock(HttpResponse.class);
        doReturn(200).when(response).statusCode();
        doReturn(annotationJSONParser.parseAnnotationsToJSON(new ArrayList<Annotation>())).when(response).body();
        doReturn(response).when(connector).getSubscriptions(any());
        message.setText("B");
        articleHandler.processResponse(message,bot);
        message.setText("1");
        articleHandler.processResponse(message,bot);
        message.setText("A");
        articleHandler.processResponse(message,bot);
        verify(bot).sendInteraction(any(User.class), eq("AVAILABLE_ARTICLES"));
        verify(bot).sendArticleList(any(User.class), any(List.class));
        verify(bot).sendInteraction(any(User.class), eq("CHOSEN_ARTICLE"), eq(1));
        verify(bot).sendInteraction(any(User.class), eq("CHOOSE_OWN_ARTICLES_ACTION"));
        verify(bot).sendInteraction(any(User.class), eq("NO_SUBSCRIPTIONS"));
    }

    @Test
    public void testArticleViewSubscriptorsWrong() throws HttpException, IOException, URISyntaxException, InterruptedException {
        HttpResponse response =  mock(HttpResponse.class);
        doReturn(400).when(response).statusCode();
        doReturn(response).when(connector).getSubscriptions(any());
        message.setText("B");
        articleHandler.processResponse(message,bot);
        message.setText("1");
        articleHandler.processResponse(message,bot);
        message.setText("A");
        articleHandler.processResponse(message,bot);
        verify(bot).sendInteraction(any(User.class), eq("AVAILABLE_ARTICLES"));
        verify(bot).sendArticleList(any(User.class), any(List.class));
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
        verify(bot).sendArticleList(any(User.class), any(List.class));
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
        verify(bot).sendArticleList(any(User.class), any(List.class));
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
        verify(bot).sendArticleList(any(User.class), any(List.class));
        verify(bot).sendInteraction(any(User.class), eq("CHOSEN_ARTICLE"), eq(1));
        verify(bot).sendInteraction(any(User.class), eq("CHOOSE_OWN_ARTICLES_ACTION"));
        verify(bot).sendAnnotationList(any(User.class), any(List.class));
    }
    @Test
    public void testShowArticlesWithoutLogin() throws HttpException {
        articleHandler.setArticleType(ArticleType.TODOS);
        articleHandler.processResponse(message,bot);
        verify(bot).sendInteraction(any(User.class), eq("AVAILABLE_ARTICLES"));
        verify(bot).sendArticleList(any(User.class), any(List.class));
    }
}
