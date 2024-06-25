package ar.edu.utn.frba.tests.handler;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.tacsbot.api.report.ReportApi;
import org.tacsbot.api.report.impl.ReportApiConnection;
import org.tacsbot.api.report.impl.ReportHttpConnector;
import org.tacsbot.bot.MyTelegramBot;
import org.tacsbot.handlers.impl.ReportHandler;
import org.tacsbot.handlers.impl.reports.*;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ReportHandlerTest {

    private Message message;

    private MyTelegramBot bot;
    ReportHandler reportHandler;
    private ReportApiConnection reportApi;
    private ReportHttpConnector reportHttpConnector;
    HttpResponse<String> response;
    @Before
    public void mockMessageApiAndBot() throws URISyntaxException, IOException, InterruptedException {
        // message
        message = new Message();
        message.setFrom(new User());

        // api
        reportApi = new ReportApiConnection();
        reportHttpConnector = mock(ReportHttpConnector.class);
        reportApi.setReportHttpConnector(reportHttpConnector);
        response = mock(HttpResponse.class);
        doReturn(200).when(response).statusCode();
//        doReturn("2").when(response).body();
//        doReturn(response).when(reportHttpConnector).getReportsConnector(eq("/reports/users"));
//        doReturn("2").when(response).body();
//        doReturn(3).when(reportHttpConnector).getReportsConnector(eq("/reports/articles"));
//        doReturn("2").when(response).body();
//        doReturn(4).when(reportHttpConnector).getReportsConnector(eq("/reports/articles/success"));
//        doReturn("2").when(response).body();
//        doReturn(5).when(reportHttpConnector).getReportsConnector(eq("/reports/articles/failed"));
//        doReturn("2").when(response).body();
//        doReturn(6).when(reportHttpConnector).getReportsConnector(eq("/reports/engaged_users"));

        reportHandler= new ReportHandler(reportApi);
        // bot
        bot = mock(MyTelegramBot.class);
        doNothing().when(bot).sendInteraction(any(), anyString());

    }

    private void testGenericReport(ReportHandler reportHandler, String interaction, int reportValue, Class<? extends ReportCommand> reportCommandClass){

        // verify the correct class has been processed
        Assert.assertEquals(reportHandler.getReportService().getClass(), reportCommandClass);

        // verify the response was sent
        verify(bot, times(1)).sendInteraction(any(), same(interaction), same(reportValue));
    }

    @Test
    public void usersCountReportTest() throws URISyntaxException, IOException, InterruptedException {
        doReturn("2").when(response).body();
        doReturn(response).when(reportHttpConnector).getReportsConnector(eq("/reports/users"));

        message.setText("B");
        reportHandler.processResponse(message, bot);
        testGenericReport(reportHandler, "USERS_COUNT_REPORT", 2, UsersCountCommand.class);
    }

    @Test
    public void engagedUsersCountReportTest() throws URISyntaxException, IOException, InterruptedException {
        doReturn("4").when(response).body();
        doReturn(response).when(reportHttpConnector).getReportsConnector(eq("/reports/engaged_users"));
        message.setText("A");
        reportHandler.processResponse(message, bot);
        testGenericReport(reportHandler, "ENGAGED_USERS_COUNT_REPORT", 4, EngagedUsersCommand.class);
    }

    @Test
    public void articlesCountReportTest() throws URISyntaxException, IOException, InterruptedException {
        doReturn("3").when(response).body();
        doReturn(response).when(reportHttpConnector).getReportsConnector(eq("/reports/articles"));
        message.setText("C");
        reportHandler.processResponse(message, bot);
        testGenericReport(reportHandler, "ARTICLES_COUNT_REPORT", 3, ArticlesCountCommand.class);
    }

    @Test
    public void successFulArticlesCountReportTest() throws URISyntaxException, IOException, InterruptedException {
        doReturn("6").when(response).body();
        doReturn(response).when(reportHttpConnector).getReportsConnector(eq("/reports/articles/success"));
        message.setText("D");
        reportHandler.processResponse(message, bot);
        testGenericReport(reportHandler, "SUCC_ARTICLES_COUNT_REPORT", 6, SuccessfulArticlesCountComamnd.class);
    }

    @Test
    public void failedArticlesCountReportTest() throws URISyntaxException, IOException, InterruptedException {
        doReturn("5").when(response).body();
        doReturn(response).when(reportHttpConnector).getReportsConnector(eq("/reports/articles/failed"));
        message.setText("E");
        reportHandler.processResponse(message, bot);
        testGenericReport(reportHandler, "FAILED_ARTICLES_COUNT_REPORT", 5, FailedArticlesCountComamnd.class);
    }

    @Test
    public void wrongOptionReportTest(){
        ReportHandler reportHandler = new ReportHandler(reportApi);
        message.setText("WRONG_OPTION");
        reportHandler.processResponse(message, bot);

        // verify no report has been set
        Assert.assertNull(reportHandler.getReportService());

        // verify the unknown response interaction has been sent
        verify(bot, times(1)).sendInteraction(any(), same("UNKNOWN_RESPONSE"));
    }

}
