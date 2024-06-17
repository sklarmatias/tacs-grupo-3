package ar.edu.utn.frba.tests.bot;

import ar.edu.utn.frba.tests.helpers.ModelEqualsHelper;
import org.junit.Assert;
import org.junit.Test;
import org.tacsbot.bot.MyTelegramBot;
import org.tacsbot.model.User;

import java.io.IOException;

public class MyTelegramBotTest {

    private MyTelegramBot myTelegramBot;
    private User user = new User(
            "abcdefghi",
            "Thiago",
            "Cabrera",
            "thiago@tacs.com",
            null
    );

    private Long chatId = 1234546789L;

    @Test
    public void loginSavesDoubleMapping() throws IOException {
        myTelegramBot = new MyTelegramBot();

        myTelegramBot.loginUser(chatId, user);

        Assert.assertEquals(chatId, myTelegramBot.getRedisService().getChatIdOfUser(user.getId()));

        ModelEqualsHelper.assertEquals(user,myTelegramBot.getRedisService().getUser(chatId));

    }

    @Test
    public void logoutDeletesDoubleMapping() throws IOException {
        myTelegramBot = new MyTelegramBot();

        myTelegramBot.loginUser(chatId, user);

        Assert.assertEquals(chatId, myTelegramBot.getRedisService().getChatIdOfUser(user.getId()));

        ModelEqualsHelper.assertEquals(user,myTelegramBot.getRedisService().getUser(chatId));


    }

}
