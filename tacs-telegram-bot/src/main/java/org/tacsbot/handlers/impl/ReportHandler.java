package org.tacsbot.handlers.impl;

import lombok.Getter;
import org.tacsbot.api.report.ReportApi;
import org.tacsbot.bot.MyTelegramBot;
import org.tacsbot.handlers.CommandsHandler;
import org.tacsbot.handlers.impl.reports.*;
import org.telegram.telegrambots.meta.api.objects.Message;

public class ReportHandler implements CommandsHandler {

    @Getter
    private ReportCommand reportService;

    private ReportApi reportApi;

    public ReportHandler(ReportApi reportApi){
        this.reportApi = reportApi;
    }

    private void setReportService(String message){
        switch (message){
            case "A":
                reportService = new EngagedUsersCommand(reportApi);
                break;
            case "B":
                reportService = new UsersCountCommand(reportApi);
                break;
            case "C":
                reportService = new ArticlesCountCommand(reportApi);
                break;
            case "D":
                reportService = new SuccessfulArticlesCountComamnd(reportApi);
                break;
            case "E":
                reportService = new FailedArticlesCountComamnd(reportApi);
                break;
        }
    }

    @Override
    public void processResponse(Message message, MyTelegramBot bot) {
            if (reportService == null){
                setReportService(message.getText());
                if (reportService == null){
                    bot.sendInteraction(message.getFrom(), "UNKNOWN_RESPONSE");
                    return;
                }
            }
            reportService.processReport(message, bot);
    }
}
