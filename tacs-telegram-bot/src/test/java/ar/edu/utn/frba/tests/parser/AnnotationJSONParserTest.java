package ar.edu.utn.frba.tests.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.Assert;
import org.junit.Test;
import org.tacsbot.model.Annotation;
import org.tacsbot.model.User;
import org.tacsbot.parser.annotation.impl.AnnotationJSONParser;
import org.tacsbot.parser.user.impl.UserJSONParser;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AnnotationJSONParserTest {
    private final String testJSON = """
                [{
                  "user": {
                    "name" : "thiago",
                    "surname" : "cabrera",
                    "email" : "thiago@tacs.com",
                    "pass" : "tacs2024"
                  },
                  "created_at":"2025-01-01 00:00:00"
                }]""";

    private final Annotation annotation = new Annotation(new User(
            null,
            "thiago",
            "cabrera",
            "thiago@tacs.com",
            "tacs2024"
    ), new SimpleDateFormat("yyyy-MM-dd").parse("2025-01-01"));

    public AnnotationJSONParserTest() throws ParseException {
    }


    private void assertUsersEqual(User user1, User user2){
        Assert.assertEquals(user1.getId(), user2.getId());
        Assert.assertEquals(user1.getName(), user2.getName());
        Assert.assertEquals(user1.getSurname(), user2.getSurname());
        Assert.assertEquals(user1.getEmail(), user2.getEmail());
        Assert.assertEquals(user1.getPass(), user2.getPass());
    }
    private void assertAnnotationEqual(Annotation annotation1, Annotation annotation2){
        Assert.assertEquals(annotation1.getDate(), annotation2.getDate());
        assertUsersEqual(annotation1.getUser(),annotation2.getUser());
    }

    @Test
    public void JSONToAnnotation(){
        List<Annotation> convertedAnnotations = new AnnotationJSONParser().parseJSONToAnnotation(testJSON);
        assertAnnotationEqual(annotation,convertedAnnotations.getFirst());
    }

    @Test
    public void invalidJSONToAnnotationTest(){
        String invalidTestJSON = """
                [{
                  "user": {
                    "name" : "thiago",
                    "surname" : "cabrera",
                    "email" : "thiago@tacs.com",
                    "pass" : "tacs2024"
                  },
                  "created_at":"2025-01-01"
                }]""";
        Assert.assertThrows(IllegalArgumentException.class,  () ->{
            new AnnotationJSONParser().parseJSONToAnnotation(invalidTestJSON);
        });
    }
}
