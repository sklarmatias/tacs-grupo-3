package org.tacsbot.handlers.impl.reports;

import lombok.Getter;
import org.tacsbot.api.report.ReportApi;
import org.tacsbot.bot.MyTelegramBot;
import org.telegram.telegrambots.meta.api.objects.Message;

@Getter
public abstract class ReportCommand {

    private final ReportApi reportApi;

    public ReportCommand(ReportApi reportApi){
        this.reportApi = reportApi;
    }

    public abstract void processReport(Message message, MyTelegramBot telegramBot);

    public void processSimpleReport(Message message, MyTelegramBot telegramBot, String interaction, int number){
        telegramBot.sendInteraction(message.getFrom(), interaction, number);
    }

}
