package ar.edu.utn.frba.tests.handler;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.tacsbot.api.report.ReportApi;
import org.tacsbot.api.report.impl.ReportApiImpl;
import org.tacsbot.bot.MyTelegramBot;
import org.tacsbot.handlers.impl.ReportHandler;
import org.tacsbot.handlers.impl.reports.*;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ReportHandlerTest {

    private Message message;

    private MyTelegramBot bot;

    private ReportApi reportApi;


    @Before
    public void mockMessageApiAndBot(){
        // message
        message = new Message();
        message.setFrom(new User());

        // api
        reportApi = mock(ReportApiImpl.class);
        when(reportApi.getUsersCount()).thenReturn(2);
        when(reportApi.getArticlesCount()).thenReturn(3);
        when(reportApi.getEngagedUsersCount()).thenReturn(4);
        when(reportApi.getFailedArticlesCount()).thenReturn(5);
        when(reportApi.getSuccessfulArticlesCount()).thenReturn(6);

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
    public void usersCountReportTest(){
        ReportHandler reportHandler = new ReportHandler(reportApi);
        message.setText("B");
        reportHandler.processResponse(message, bot);
        testGenericReport(reportHandler, "USERS_COUNT_REPORT", 2, UsersCountCommand.class);
    }

    @Test
    public void engagedUsersCountReportTest(){
        ReportHandler reportHandler = new ReportHandler(reportApi);
        message.setText("A");
        reportHandler.processResponse(message, bot);
        testGenericReport(reportHandler, "ENGAGED_USERS_COUNT_REPORT", 4, EngagedUsersCommand.class);
    }

    @Test
    public void articlesCountReportTest(){
        ReportHandler reportHandler = new ReportHandler(reportApi);
        message.setText("C");
        reportHandler.processResponse(message, bot);
        testGenericReport(reportHandler, "ARTICLES_COUNT_REPORT", 3, ArticlesCountCommand.class);
    }

    @Test
    public void successFulArticlesCountReportTest(){
        ReportHandler reportHandler = new ReportHandler(reportApi);
        message.setText("D");
        reportHandler.processResponse(message, bot);
        testGenericReport(reportHandler, "SUCC_ARTICLES_COUNT_REPORT", 6, SuccessfulArticlesCountComamnd.class);
    }

    @Test
    public void failedArticlesCountReportTest(){
        ReportHandler reportHandler = new ReportHandler(reportApi);
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
