package org.tacsbot.handlers.impl.reports;

import org.tacsbot.api.report.ReportApi;
import org.tacsbot.bot.MyTelegramBot;
import org.telegram.telegrambots.meta.api.objects.Message;

public class UsersCountCommand extends ReportCommand{
    public UsersCountCommand(ReportApi reportApi) {
        super(reportApi);
    }

    @Override
    public void processReport(Message message, MyTelegramBot telegramBot) {
        processSimpleReport(message, telegramBot, "USERS_COUNT_REPORT", getReportApi().getUsersCount());
    }
}
