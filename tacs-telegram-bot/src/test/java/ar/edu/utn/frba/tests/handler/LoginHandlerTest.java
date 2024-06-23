package ar.edu.utn.frba.tests.handler;

import org.apache.http.HttpException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.tacsbot.api.user.UserApi;
import org.tacsbot.api.user.impl.UserApiConnection;
import org.tacsbot.bot.MyTelegramBot;
import org.tacsbot.handlers.impl.LoginHandler;
import org.tacsbot.model.Article;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import javax.naming.AuthenticationException;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class LoginHandlerTest {
    private Message message;
    private LoginHandler loginHandler;
    private MyTelegramBot bot;
    private UserApi api;
    @Before
    public void mockMessageApiAndBot() throws IOException {
        // message
        message = new Message();
        message.setFrom(new User());
        message.setChat(new Chat(123L,"type"));

        api = mock(UserApiConnection.class);
        // bot
        bot = mock(MyTelegramBot.class);
        doNothing().when(bot).logInUser(any(),any());
        doNothing().when(bot).sendInteraction(any(), anyString());
        loginHandler = new LoginHandler(1234L);
        loginHandler.setUserApiConnection(api);
    }
    @Test
    public void testEmailOk() throws IOException {
        message.setText("abc@abc.com");
        loginHandler.processResponse(message,bot);
        verify(bot).sendInteraction(any(User.class), eq("LOGIN_PASS"));
        Assert.assertEquals(message.getText(),loginHandler.getUser().getEmail());
    }
    @Test
    public void testEmailWrong() throws IOException {
        message.setText("abc");
        loginHandler.processResponse(message,bot);
        verify(bot).sendInteraction(any(User.class), eq("ERROR_EMAIL_INVALID"));
        verify(bot).sendInteraction(any(User.class), eq("LOGIN_EMAIL"));
    }
    @Test
    public void testLoginOk() throws IOException, AuthenticationException {
        org.tacsbot.model.User returnUser = new org.tacsbot.model.User();
        returnUser.setName("name");
        doReturn(returnUser).when(api).logIn(any(),any());
        message.setText("abc@abc.com");
        loginHandler.processResponse(message,bot);
        message.setText("123456");
        loginHandler.processResponse(message,bot);
        verify(bot).sendInteraction(any(User.class), eq("WELCOME_LOGGED_IN"),eq("name"));
        verify(api).logIn(eq(loginHandler.getUser().getEmail()),eq(loginHandler.getUser().getPass()));
    }
    @Test
    public void testLoginFail() throws IOException, AuthenticationException {
        doThrow(AuthenticationException.class).when(api).logIn(any(),any());
        message.setText("abc@abc.com");
        loginHandler.processResponse(message,bot);
        message.setText("123456");
        loginHandler.processResponse(message,bot);
        verify(bot).sendInteraction(any(User.class), eq("WRONG_CREDENTIALS"));
    }
}
