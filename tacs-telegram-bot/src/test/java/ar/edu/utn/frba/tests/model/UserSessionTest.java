package ar.edu.utn.frba.tests.model;

import org.junit.Assert;
import org.junit.Test;
import org.tacsbot.model.User;
import org.tacsbot.model.UserSession;

public class UserSessionTest {

    private UserSession userSession1 = new UserSession(
            "1234567",
            "thiago",
            "cabrera",
            "thiago@tacs.com"
    );

    private UserSession userSession2 = new UserSession(
            "qwertyu",
            "matias",
            "magarzo",
            "matias@tacs.com"
    );

    @Test
    public void userSessionEqualsTest(){

        UserSession anotherUser = new UserSession(
                "1234567",
                "thiago",
                "cabrera",
                "thiago@tacs.com"
        );

        Assert.assertEquals(userSession1, anotherUser);
    }

    @Test
    public void userSessionNotEqualsTest(){
        Assert.assertNotEquals(userSession1, userSession2);
    }

    @Test
    public void userSessionNotEqualsInvalidClassTest(){

        User user = new User("123455", "thiago");

        Assert.assertNotEquals(userSession1, user);
    }

}
