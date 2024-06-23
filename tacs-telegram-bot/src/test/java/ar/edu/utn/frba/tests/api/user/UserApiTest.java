package ar.edu.utn.frba.tests.api;

import org.junit.Before;
import org.junit.Test;
import org.tacsbot.api.user.impl.UserApiConnection;

public class UserApiTest {

    private UserApiConnection userApiConnection;

    @Before
    public void befEach(){
        userApiConnection = new UserApiConnection();
    }

    @Test
    public void logInSuccessTest(){
    }

    @Test
    public void logInFailedTest(){

    }

    @Test
    public void logInErrorTest(){

    }

    @Test
    public void registerSuccessTest(){

    }

    @Test
    public void registerErrorTest(){

    }

}
