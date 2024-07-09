package ar.edu.utn.frba.tests.handler;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.tacsbot.api.user.UserApi;
import org.tacsbot.api.user.impl.UserApiConnection;
import org.tacsbot.api.user.impl.UserHttpConnector;
import org.tacsbot.api.utils.ApiHttpConnector;
import org.tacsbot.bot.MyTelegramBot;
import org.tacsbot.handlers.impl.ArticleCreationStep;
import org.tacsbot.handlers.impl.LoginHandler;
import org.tacsbot.handlers.impl.RegisterHandler;
import org.tacsbot.parser.user.impl.UserJSONParser;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class RegisterHandlerTest {
    private Message message;
    private RegisterHandler registerHandler;
    private MyTelegramBot bot;
    private UserApiConnection api;
    private ApiHttpConnector connector;
    private UserJSONParser parser;
    @Before
    public void mockMessageApiAndBot() throws IOException {
        // message
        message = new Message();
        message.setFrom(new User());
        message.setChat(new Chat(123L,"type"));
        parser = new UserJSONParser();
        api = new UserApiConnection();
        connector = mock(ApiHttpConnector.class);
        api.setApiHttpConnector(connector);
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
    public void testRegisterOk() throws IOException, URISyntaxException, InterruptedException {
        HttpResponse response =  mock(HttpResponse.class);
        doReturn(201).when(response).statusCode();
        doReturn(response).when(connector).post(any(), any());
        message.setText("juan");
        registerHandler.processResponse(message,bot);
        message.setText("perez");
        registerHandler.processResponse(message,bot);
        message.setText("abc@abc.com");
        registerHandler.processResponse(message,bot);
        message.setText("123456");
        registerHandler.processResponse(message,bot);
        verify(bot).sendInteraction(any(User.class), eq("REGISTER_COMPLETED"));
        verify(connector).post(any(), any());
    }
    @Test
    public void testRegisterFailEmail() throws IOException, URISyntaxException, InterruptedException {
        org.tacsbot.model.User returnUser = new org.tacsbot.model.User();
        returnUser.setEmail("email");
        String body = String.format("Error! Email %s already in use", returnUser.getEmail());
        HttpResponse response =  mock(HttpResponse.class);
        doReturn(400).when(response).statusCode();
        doReturn(body).when(response).body();
        doReturn(response).when(connector).post(any(), any());
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
    public void testRegisterFailIo() throws IOException, URISyntaxException, InterruptedException {
        HttpResponse response =  mock(HttpResponse.class);
        doReturn(401).when(response).statusCode();
        doReturn(response).when(connector).post(any(), any());
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
