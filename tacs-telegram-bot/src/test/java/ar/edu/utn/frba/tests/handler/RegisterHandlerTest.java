package ar.edu.utn.frba.tests.handler;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.tacsbot.api.user.UserApi;
import org.tacsbot.api.user.impl.UserApiConnection;
import org.tacsbot.bot.MyTelegramBot;
import org.tacsbot.handlers.impl.ArticleCreationStep;
import org.tacsbot.handlers.impl.LoginHandler;
import org.tacsbot.handlers.impl.RegisterHandler;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class RegisterHandlerTest {
    private Message message;
    private RegisterHandler registerHandler;
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
        registerHandler = new RegisterHandler(1234L);
        registerHandler.setUserApiConnection(api);
    }
    @Test
    public void testNameOk() throws IOException {
        message.setText("juan");
        registerHandler.processResponse(message,bot);
        verify(bot).sendInteraction(any(User.class), eq("REGISTER_SURNAME"));
        Assert.assertEquals(message.getText(),registerHandler.getUser().getName());
    }
    @Test
    public void testNameEmpty() throws IOException {
        message.setText("");
        registerHandler.processResponse(message,bot);
        verify(bot).sendInteraction(any(User.class), eq("REGISTER_NAME_EMPTY"));
        verify(bot).sendInteraction(any(User.class), eq("REGISTER_NAME"));
    }
    @Test
    public void testSurnameOk() throws IOException {
        message.setText("perez");
        registerHandler.setCurrentStep(RegisterHandler.RegistrationStep.REQUEST_USER_SURNAME);
        registerHandler.processResponse(message,bot);
        verify(bot).sendInteraction(any(User.class), eq("REGISTER_EMAIL"));
        Assert.assertEquals(message.getText(),registerHandler.getUser().getSurname());
    }
    @Test
    public void testSurnameEmpty() throws IOException {
        message.setText("");
        registerHandler.setCurrentStep(RegisterHandler.RegistrationStep.REQUEST_USER_SURNAME);
        registerHandler.processResponse(message,bot);
        verify(bot).sendInteraction(any(User.class), eq("REGISTER_SURNAME_EMPTY"));
        verify(bot).sendInteraction(any(User.class), eq("REGISTER_SURNAME"));
    }
    @Test
    public void testEmailOk() throws IOException {
        message.setText("abc@abc.com");
        registerHandler.setCurrentStep(RegisterHandler.RegistrationStep.REQUEST_EMAIL);
        registerHandler.processResponse(message,bot);
        verify(bot).sendInteraction(any(User.class), eq("REGISTER_PASS"));
        Assert.assertEquals(message.getText(),registerHandler.getUser().getEmail());
    }
    @Test
    public void testEmailWrong() throws IOException {
        message.setText("abc");
        registerHandler.setCurrentStep(RegisterHandler.RegistrationStep.REQUEST_EMAIL);
        registerHandler.processResponse(message,bot);
        verify(bot).sendInteraction(any(User.class), eq("ERROR_EMAIL_INVALID"));
        verify(bot).sendInteraction(any(User.class), eq("REGISTER_EMAIL"));
    }
    @Test
    public void testRegisterOk() throws IOException {
        doNothing().when(api).register(any(),any(),any(),any());
        message.setText("juan");
        registerHandler.processResponse(message,bot);
        message.setText("perez");
        registerHandler.processResponse(message,bot);
        message.setText("abc@abc.com");
        registerHandler.processResponse(message,bot);
        message.setText("123456");
        registerHandler.processResponse(message,bot);
        verify(bot).sendInteraction(any(User.class), eq("REGISTER_COMPLETED"));
        verify(api).register(eq(registerHandler.getUser().getName()),eq(registerHandler.getUser().getSurname()),eq(registerHandler.getUser().getEmail()),eq(registerHandler.getUser().getPass()));
    }
    @Test
    public void testRegisterFail() throws IOException {
        doThrow(IllegalArgumentException.class).when(api).register(any(),any(),any(),any());
        message.setText("juan");
        registerHandler.processResponse(message,bot);
        message.setText("perez");
        registerHandler.processResponse(message,bot);
        message.setText("abc@abc.com");
        registerHandler.processResponse(message,bot);
        message.setText("123456");
        registerHandler.processResponse(message,bot);
        verify(bot).sendInteraction(any(User.class), eq("REGISTER_INVALID"));
    }
    @Test
    public void testRegisterFailIo() throws IOException {
        doThrow(IOException.class).when(api).register(any(),any(),any(),any());
        message.setText("juan");
        registerHandler.processResponse(message,bot);
        message.setText("perez");
        registerHandler.processResponse(message,bot);
        message.setText("abc@abc.com");
        registerHandler.processResponse(message,bot);
        message.setText("123456");
        registerHandler.processResponse(message,bot);
        verify(bot).sendInteraction(any(User.class), eq("REGISTER_INVALID"));
    }
}
