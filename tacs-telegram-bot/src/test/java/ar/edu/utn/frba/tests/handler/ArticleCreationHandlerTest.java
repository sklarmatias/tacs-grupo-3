package ar.edu.utn.frba.tests.handler;

import org.apache.http.HttpException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.tacsbot.api.article.impl.ArticleApiConnection;
import org.tacsbot.api.article.impl.ArticleHttpConnector;
import org.tacsbot.bot.MyTelegramBot;
import org.tacsbot.handlers.impl.ArticleCreationHandler;
import org.tacsbot.handlers.impl.ArticleCreationStep;
import org.tacsbot.model.CostType;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ArticleCreationHandlerTest {
    private Message message;
    private ArticleCreationHandler articleCreationHandler;
    private MyTelegramBot bot;
    private ArticleApiConnection api;
    private ArticleHttpConnector connector;
    @Before
    public void mockMessageApiAndBot() throws HttpException, IOException {
        // message
        message = new Message();
        message.setFrom(new User());
        message.setChat(new Chat(123L,"type"));

        api = new ArticleApiConnection();
        connector = mock(ArticleHttpConnector.class);
        api.setArticleHttpConnector(connector);
        // bot
        bot = mock(MyTelegramBot.class);
        doNothing().when(bot).sendInteraction(any(), anyString());
        articleCreationHandler = new ArticleCreationHandler("user");
        articleCreationHandler.setArticleApi(api);
    }
    @Test
    public void testNameOk() throws HttpException, IOException {
        message.setText("articulo prueba");
        articleCreationHandler.processResponse(message, bot);
        verify(bot).sendInteraction(any(User.class), eq("ARTICLE_DEADLINE"));
        Assert.assertEquals("articulo prueba",articleCreationHandler.getArticle().getName());
    }
    @Test
    public void testNameShort() throws HttpException, IOException {
        message.setText("articulo");
        articleCreationHandler.processResponse(message, bot);
        verify(bot).sendInteraction(any(User.class), eq("ARTICLE_NAME_TOO_SHORT"));
        verify(bot).sendInteraction(any(User.class), eq("ARTICLE_NAME"));
    }
    @Test
    public void testDeadlineOk() throws HttpException, IOException, ParseException {
        message.setText("2026-01-01");
        articleCreationHandler.setCurrentStep(ArticleCreationStep.REQUEST_DEADLINE);
        articleCreationHandler.processResponse(message, bot);
        verify(bot).sendInteraction(any(User.class), eq("ARTICLE_COST_TYPE"));
        Assert.assertEquals(new SimpleDateFormat("yyyy-MM-dd").parse("2026-01-01"),articleCreationHandler.getArticle().getDeadline());
    }
    @Test
    public void testDeadlineBefore() throws HttpException, IOException {
        message.setText("2023-01-01");
        articleCreationHandler.setCurrentStep(ArticleCreationStep.REQUEST_DEADLINE);
        articleCreationHandler.processResponse(message, bot);
        verify(bot).sendInteraction(any(User.class), eq("ARTICLE_INVALID_DEADLINE"));
    }
    @Test
    public void testDeadlineInvalid() throws HttpException, IOException {
        message.setText("abc");
        articleCreationHandler.setCurrentStep(ArticleCreationStep.REQUEST_DEADLINE);
        articleCreationHandler.processResponse(message, bot);
        verify(bot).sendInteraction(any(User.class), eq("UNKNOWN_RESPONSE"));
        verify(bot).sendInteraction(any(User.class), eq("ARTICLE_DEADLINE"));
    }
    @Test
    public void testCostTypeA() throws HttpException, IOException {
        message.setText("A");
        articleCreationHandler.setCurrentStep(ArticleCreationStep.REQUEST_COST_TYPE);
        articleCreationHandler.processResponse(message, bot);
        verify(bot).sendInteraction(any(User.class), eq("ARTICLE_COST"));
        Assert.assertEquals(CostType.TOTAL,articleCreationHandler.getArticle().getCostType());
    }
    @Test
    public void testCostTypeB() throws HttpException, IOException {
        message.setText("B");
        articleCreationHandler.setCurrentStep(ArticleCreationStep.REQUEST_COST_TYPE);
        articleCreationHandler.processResponse(message, bot);
        verify(bot).sendInteraction(any(User.class), eq("ARTICLE_COST"));
        Assert.assertEquals(CostType.PER_USER,articleCreationHandler.getArticle().getCostType());
    }
    @Test
    public void testCostTypeWrong() throws HttpException, IOException {
        message.setText("C");
        articleCreationHandler.setCurrentStep(ArticleCreationStep.REQUEST_COST_TYPE);
        articleCreationHandler.processResponse(message, bot);
        verify(bot).sendInteraction(any(User.class), eq("UNKNOWN_RESPONSE"));
        verify(bot).sendInteraction(any(User.class), eq("ARTICLE_COST_TYPE"));
    }
    @Test
    public void testCostOk() throws HttpException, IOException {
        message.setText("10");
        articleCreationHandler.setCurrentStep(ArticleCreationStep.REQUEST_COST);
        articleCreationHandler.processResponse(message, bot);
        verify(bot).sendInteraction(any(User.class), eq("ARTICLE_USER_GETS"));
        Double num =Double.parseDouble("10");
        Assert.assertEquals(num,articleCreationHandler.getArticle().getCost());
    }
    @Test
    public void testCostWrong() throws HttpException, IOException {
        message.setText("A");
        articleCreationHandler.setCurrentStep(ArticleCreationStep.REQUEST_COST);
        articleCreationHandler.processResponse(message, bot);
        verify(bot).sendInteraction(any(User.class), eq("UNKNOWN_RESPONSE"));
        verify(bot).sendInteraction(any(User.class), eq("ARTICLE_COST"));
    }
    @Test
    public void testCostNegative() throws HttpException, IOException {
        message.setText("-10");
        articleCreationHandler.setCurrentStep(ArticleCreationStep.REQUEST_COST);
        articleCreationHandler.processResponse(message, bot);
        verify(bot).sendInteraction(any(User.class), eq("UNKNOWN_RESPONSE"));
        verify(bot).sendInteraction(any(User.class), eq("ARTICLE_COST"));
    }
    @Test
    public void testGetsOk() throws HttpException, IOException {
        message.setText("test");
        articleCreationHandler.setCurrentStep(ArticleCreationStep.REQUEST_USERGETS);
        articleCreationHandler.processResponse(message, bot);
        verify(bot).sendInteraction(any(User.class), eq("ARTICLE_USERS_MAX"));
        Assert.assertEquals("test",articleCreationHandler.getArticle().getUserGets());
    }
    @Test
    public void testGetsWrong() throws HttpException, IOException {
        message.setText("");
        articleCreationHandler.setCurrentStep(ArticleCreationStep.REQUEST_USERGETS);
        articleCreationHandler.processResponse(message, bot);
        verify(bot).sendInteraction(any(User.class), eq("EMPTY"));
        verify(bot).sendInteraction(any(User.class), eq("ARTICLE_USER_GETS"));
    }
    @Test
    public void testMaxUsersOk() throws HttpException, IOException {
        message.setText("10");
        articleCreationHandler.setCurrentStep(ArticleCreationStep.REQUEST_MAX_USERS);
        articleCreationHandler.processResponse(message, bot);
        verify(bot).sendInteraction(any(User.class), eq("ARTICLE_USERS_MIN"));
        Integer num =Integer.parseInt(message.getText());
        Assert.assertEquals(num,articleCreationHandler.getArticle().getUsersMax());
    }
    @Test
    public void testMaxUsersWrong() throws HttpException, IOException {
        message.setText("A");
        articleCreationHandler.setCurrentStep(ArticleCreationStep.REQUEST_MAX_USERS);
        articleCreationHandler.processResponse(message, bot);
        verify(bot).sendInteraction(any(User.class), eq("UNKNOWN_RESPONSE"));
        verify(bot).sendInteraction(any(User.class), eq("ARTICLE_USERS_MAX"));
    }
    @Test
    public void testMaxUsersNegative() throws HttpException, IOException {
        message.setText("-10");
        articleCreationHandler.setCurrentStep(ArticleCreationStep.REQUEST_MAX_USERS);
        articleCreationHandler.processResponse(message, bot);
        verify(bot).sendInteraction(any(User.class), eq("UNKNOWN_RESPONSE"));
        verify(bot).sendInteraction(any(User.class), eq("ARTICLE_USERS_MAX"));
    }
    @Test
    public void testMinUsersOk() throws HttpException, IOException {
        message.setText("100");
        articleCreationHandler.setCurrentStep(ArticleCreationStep.REQUEST_MAX_USERS);
        articleCreationHandler.processResponse(message, bot);
        message.setText("10");
        articleCreationHandler.setCurrentStep(ArticleCreationStep.REQUEST_MIN_USERS);
        articleCreationHandler.processResponse(message, bot);
        verify(bot).sendInteraction(any(User.class), eq("ARTICLE_LINK"));
        Integer num =Integer.parseInt(message.getText());
        Assert.assertEquals(num,articleCreationHandler.getArticle().getUsersMin());
    }
    @Test
    public void testMinUsersWrong() throws HttpException, IOException {
        message.setText("A");
        articleCreationHandler.setCurrentStep(ArticleCreationStep.REQUEST_MIN_USERS);
        articleCreationHandler.processResponse(message, bot);
        verify(bot).sendInteraction(any(User.class), eq("UNKNOWN_RESPONSE"));
        verify(bot).sendInteraction(any(User.class), eq("ARTICLE_USERS_MIN"));
    }
    @Test
    public void testMinUsersNegative() throws HttpException, IOException {
        message.setText("1");
        articleCreationHandler.setCurrentStep(ArticleCreationStep.REQUEST_MAX_USERS);
        articleCreationHandler.processResponse(message, bot);
        message.setText("-10");
        articleCreationHandler.setCurrentStep(ArticleCreationStep.REQUEST_MIN_USERS);
        articleCreationHandler.processResponse(message, bot);
        verify(bot).sendInteraction(any(User.class), eq("UNKNOWN_RESPONSE"));
    }

    @Test
    public void testMinUsersBigger() throws HttpException, IOException {
        message.setText("1");
        articleCreationHandler.setCurrentStep(ArticleCreationStep.REQUEST_MAX_USERS);
        articleCreationHandler.processResponse(message, bot);
        message.setText("2");
        articleCreationHandler.processResponse(message, bot);
        verify(bot).sendInteraction(any(User.class), eq("ARTICLE_INVALID_USERS_MIN"),eq(articleCreationHandler.getArticle().getUsersMax()));
    }
    @Test
    public void testLinkOk() throws HttpException, IOException {
        message.setText("aaa");
        articleCreationHandler.setCurrentStep(ArticleCreationStep.REQUEST_LINK);
        articleCreationHandler.processResponse(message, bot);
        verify(bot).sendInteraction(any(User.class), eq("ARTICLE_IMAGE"));
        Assert.assertEquals("aaa",articleCreationHandler.getArticle().getLink());
    }
    @Test
    public void testImageEmpty() throws HttpException, IOException {
        message.setText(null);
        articleCreationHandler.setCurrentStep(ArticleCreationStep.REQUEST_IMAGE);
        articleCreationHandler.processResponse(message, bot);
        verify(bot).sendInteraction(any(User.class), eq("ARTICLE_IMAGE_INVALID"));
    }
    @Test
    public void testCreateArticleOk() throws HttpException, IOException, URISyntaxException, InterruptedException {
        HttpResponse response =  mock(HttpResponse.class);
        doReturn(201).when(response).statusCode();
        doReturn(response).when(connector).createArticleConnector(any(),any());
        createTestArticle();
        verify(bot).sendInteraction(any(User.class), eq("ARTICLE_CREATED"));
        verify(connector).createArticleConnector(any(),any());
    }
    @Test
    public void testCreateArticleFail() throws HttpException, IOException, URISyntaxException, InterruptedException {
        HttpResponse response =  mock(HttpResponse.class);
        doReturn(400).when(response).statusCode();
        doReturn(response).when(connector).createArticleConnector(any(),any());
        createTestArticle();
        verify(bot).sendInteraction(any(User.class), eq("ARTICLE_NOT_CREATED"));
        verify(connector).createArticleConnector(any(),any());
    }
    private void createTestArticle() throws HttpException, IOException {
        message.setText("articulo prueba");
        articleCreationHandler.processResponse(message, bot);
        message.setText("2027-01-04");
        articleCreationHandler.processResponse(message, bot);
        message.setText("A");
        articleCreationHandler.processResponse(message, bot);
        message.setText("20");
        articleCreationHandler.processResponse(message, bot);
        message.setText("aaa");
        articleCreationHandler.processResponse(message, bot);
        message.setText("2");
        articleCreationHandler.processResponse(message, bot);
        message.setText("1");
        articleCreationHandler.processResponse(message, bot);
        message.setText("A");
        articleCreationHandler.processResponse(message, bot);
        message.setText("A");
        articleCreationHandler.processResponse(message, bot);
    }
}
