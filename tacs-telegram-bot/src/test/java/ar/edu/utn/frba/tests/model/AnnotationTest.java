package ar.edu.utn.frba.tests.model;

import org.junit.Assert;
import org.junit.Test;
import org.tacsbot.model.Annotation;
import org.tacsbot.model.User;

import java.util.Date;

public class AnnotationTest {

    @Test
    public void toStringTest(){
        Annotation annotation = new Annotation(new User(null, "Thiago", "Cabrera", "thiago@tacs.com", null), new Date(1716782052L * 1000));
        String expected = """
                (27/05/2024)
                Cabrera, Thiago (thiago@tacs.com)""";
        Assert.assertEquals(expected, annotation.toString());
    }

}
