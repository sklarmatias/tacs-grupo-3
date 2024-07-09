package ar.edu.utn.frba.tests.handler;

import org.apache.http.HttpException;
import org.junit.Before;
import org.junit.Test;
import org.tacsbot.api.article.impl.ArticleApiConnection;
import org.tacsbot.api.utils.ApiHttpConnector;
import org.tacsbot.bot.MyTelegramBot;
import org.tacsbot.cache.CacheService;
import org.tacsbot.cache.impl.RedisService;
import org.tacsbot.exceptions.UnauthorizedException;
import org.tacsbot.handlers.impl.ArticleHandler;
import org.tacsbot.handlers.impl.ArticleType;
import org.tacsbot.model.Annotation;
import org.tacsbot.model.Article;
import org.tacsbot.model.UserSession;
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
    private ApiHttpConnector connector;
    private ArticleJSONParser articleJSONParser = new ArticleJSONParser();
    private AnnotationJSONParser annotationJSONParser = new AnnotationJSONParser();
    @Before
    public void mockMessageApiAndBot() throws IOException, HttpException, URISyntaxException, InterruptedException, UnauthorizedException {
        // message
        message = new Message();
        message.setFrom(new User());
        message.setChat(new Chat(123L,"type"));

        api = new ArticleApiConnection();
        connector = mock(ApiHttpConnector.class);
        api.setApiHttpConnector(connector);
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
        UserSession userSession = new UserSession("abcdefg", "thiago", "cabrera", "thiago@tacs.com");
        doReturn(userSession).when(cacheService).getSession(any());
        doReturn(cacheService).when(bot).getCacheService();

        articleList = new ArrayList<>();
        Article article = new Article();
        article.setId("articleid");
        articleList.add(article);
        HttpResponse response1 =  mock(HttpResponse.class);
        doReturn(200).when(response1).statusCode();
        doReturn(articleJSONParser.parseArticleListToJSON(articleList)).when(response1).body();
        doReturn(response1).when(connector).get(any());

        annotationList = new ArrayList<>();
        annotationList.add(new Annotation());
        HttpResponse response2 =  mock(HttpResponse.class);
        doReturn(200).when(response2).statusCode();
        doReturn(annotationJSONParser.parseAnnotationsToJSON(annotationList)).when(response2).body();
        doReturn(response2).when(connector).get(any());

        HttpResponse response3 =  mock(HttpResponse.class);
        doReturn(200).when(response3).statusCode();
        doReturn(articleJSONParser.parseArticleToJSON(article)).when(response3).body();
        doReturn(response3).when(connector).patch(any(),any(), any());

        HttpResponse response4 =  mock(HttpResponse.class);
        doReturn(200).when(response4).statusCode();
        doReturn(response4).when(connector).post(any(),any());


    }
    @Test
    public void testArticleTypeAEmpty() throws HttpException, IOException, URISyntaxException, InterruptedException, UnauthorizedException {
        List<Article> articleList = new ArrayList<>();
        HttpResponse response1 =  mock(HttpResponse.class);
        doReturn(200).when(response1).statusCode();
        doReturn(articleJSONParser.parseArticleListToJSON(articleList)).when(response1).body();
        doReturn(response1).when(connector).post(any(), any());
        message.setText("A");
        articleHandler.processResponse(message,bot);
        verify(bot).sendInteraction(any(User.class), eq("NO_ARTICLES"));
    }
    @Test
    public void testArticleTypeBEmpty() throws HttpException, IOException, URISyntaxException, InterruptedException, UnauthorizedException {
        List<Article> articleList = new ArrayList<>();
        HttpResponse response1 =  mock(HttpResponse.class);
        doReturn(200).when(response1).statusCode();
        doReturn(articleJSONParser.parseArticleListToJSON(articleList)).when(response1).body();
        doReturn(response1).when(connector).post(any(), any());
        message.setText("B");
        articleHandler.processResponse(message,bot);
        verify(bot).sendInteraction(any(User.class), eq("NO_ARTICLES"));
    }
    @Test
    public void testArticleTypeWrong() throws HttpException, UnauthorizedException {
        message.setText("C");
        articleHandler.processResponse(message,bot);
        verify(bot).sendInteraction(any(User.class), eq("UNKNOWN_RESPONSE"));
    }
    @Test
    public void testArticleWrongIndex() throws HttpException, UnauthorizedException {
        message.setText("B");
        articleHandler.processResponse(message,bot);
        message.setText("B");
        articleHandler.processResponse(message,bot);
        verify(bot).sendInteraction(any(User.class), eq("UNKNOWN_RESPONSE"));
    }
    @Test
    public void testArticleIndexOutOfBound() throws HttpException, UnauthorizedException {
        message.setText("B");
        articleHandler.processResponse(message,bot);
        message.setText("4");
        articleHandler.processResponse(message,bot);
        verify(bot).sendInteraction(any(User.class), eq("ARTICLE_INVALID_INDEX"));
    }
    @Test
    public void testArticleSubscribeCancel() throws HttpException, UnauthorizedException {
        message.setText("A");
        articleHandler.processResponse(message,bot);
        message.setText("1");
        articleHandler.processResponse(message,bot);
        message.setText("B");
        articleHandler.processResponse(message,bot);
        verify(bot).sendInteraction(any(User.class), eq("CANCELLATION"),eq("name"));
    }

    @Test
    public void testArticleSubscribeWrongInput() throws HttpException, UnauthorizedException {
        message.setText("A");
        articleHandler.processResponse(message,bot);
        message.setText("1");
        articleHandler.processResponse(message,bot);
        message.setText("C");
        articleHandler.processResponse(message,bot);
        verify(bot).sendInteraction(any(User.class), eq("UNKNOWN_RESPONSE"));
    }

    @Test
    public void testArticleOwnArticleWrongInput() throws HttpException, UnauthorizedException {
        message.setText("B");
        articleHandler.processResponse(message,bot);
        message.setText("1");
        articleHandler.processResponse(message,bot);
        message.setText("C");
        articleHandler.processResponse(message,bot);
        verify(bot).sendInteraction(any(User.class), eq("UNKNOWN_RESPONSE"));
    }
    @Test
    public void testArticleSubscribeFail() throws HttpException, URISyntaxException, IOException, InterruptedException, UnauthorizedException {
        HttpResponse response =  mock(HttpResponse.class);
        doReturn(400).when(response).statusCode();
        doReturn(response).when(connector).post(any(),any());
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
    public void testArticleCloseFail() throws HttpException, URISyntaxException, IOException, InterruptedException, UnauthorizedException {
        HttpResponse response =  mock(HttpResponse.class);
        doReturn(400).when(response).statusCode();
        doReturn(response).when(connector).patch(any(),any(), any());
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
    public void testArticleViewSubscriptorsNoSubscriptions() throws HttpException, IOException, URISyntaxException, InterruptedException, UnauthorizedException {
        HttpResponse response =  mock(HttpResponse.class);
        doReturn(200).when(response).statusCode();
        doReturn(annotationJSONParser.parseAnnotationsToJSON(new ArrayList<Annotation>())).when(response).body();
        doReturn(response).when(connector).get(any());
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
    public void testArticleViewSubscriptorsWrong() throws HttpException, IOException, URISyntaxException, InterruptedException, UnauthorizedException {
        HttpResponse response =  mock(HttpResponse.class);
        doReturn(400).when(response).statusCode();
        doReturn(response).when(connector).get(any());
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
    public void testArticleSubscribeOk() throws HttpException, UnauthorizedException {
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
    public void testArticleCloseOk() throws HttpException, UnauthorizedException {
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
    public void testArticleViewSubscriptorsOk() throws HttpException, UnauthorizedException {
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
    public void testShowArticlesWithoutLogin() throws HttpException, UnauthorizedException {
        articleHandler.setArticleType(ArticleType.TODOS);
        articleHandler.processResponse(message,bot);
        verify(bot).sendInteraction(any(User.class), eq("AVAILABLE_ARTICLES"));
        verify(bot).sendArticleList(any(User.class), any(List.class));
    }
}
