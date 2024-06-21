package org.tacsbot.handlers.impl.reports;

import org.tacsbot.api.report.ReportApi;
import org.tacsbot.bot.MyTelegramBot;
import org.telegram.telegrambots.meta.api.objects.Message;

public class SuccessfulArticlesCountComamnd extends ReportCommand{
    public SuccessfulArticlesCountComamnd(ReportApi reportApi) {
        super(reportApi);
    }

    @Override
    public void processReport(Message message, MyTelegramBot telegramBot) {
        processSimpleReport(message, telegramBot, "SUCC_ARTICLES_COUNT_REPORT", getReportApi().getSuccessfulArticlesCount());
    }
}
