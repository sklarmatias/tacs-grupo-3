package ar.edu.utn.frba.tests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.tacsbot.model.User;
import org.tacsbot.service.parser.user.impl.UserJSONParser;
import java.io.IOException;

public class UserJSONParserTest {

    private final String testJSON = """
                {
                  "name" : "thiago",
                  "surname" : "cabrera",
                  "email" : "thiago@tacs.com",
                  "pass" : "tacs2024"
                }""";

    private final User user = new User(
            null,
            "thiago",
            "cabrera",
            "thiago@tacs.com",
            "tacs2024"
    );

    private void assertUsersEqual(User user1, User user2){
        Assert.assertEquals(user1.getId(), user2.getId());
        Assert.assertEquals(user1.getName(), user2.getName());
        Assert.assertEquals(user1.getSurname(), user2.getSurname());
        Assert.assertEquals(user1.getEmail(), user2.getEmail());
        Assert.assertEquals(user1.getPass(), user2.getPass());
    }

    @Test
    public void JSONToUser(){
        User convertedUser = new UserJSONParser().parseJSONToUser(testJSON);
        assertUsersEqual(user, convertedUser);
    }

    private void basicUserToJSON(User user, String json) throws IOException {
        String userJson = new UserJSONParser().parseUserToJSON(user);

        ObjectMapper mapper = new ObjectMapper();

        JsonNode tree1 = mapper.readTree(userJson);
        JsonNode tree2 = mapper.readTree(json);

        Assert.assertEquals(tree2, tree1);
    }

    @Test
    public void userToJSONTest() throws IOException {
        basicUserToJSON(user, testJSON);
    }

    @Test
    public void userToJSONNonNullIdTest() throws IOException {
        User userNonNullId = new User(
                "qwerty",
                "thiago",
                "cabrera",
                "thiago@tacs.com",
                "tacs2024"
        );
        String testJSONNonNullId = """
                {
                  "id" : "qwerty",
                  "name" : "thiago",
                  "surname" : "cabrera",
                  "email" : "thiago@tacs.com",
                  "pass" : "tacs2024"
                }""";
        basicUserToJSON(userNonNullId, testJSONNonNullId);
    }

    @Test
    public void invalidJSONToUserTest(){
        String invalidTestJSON = """
                {
                  "Name" : "thiago",
                  "Surname" : "cabrera",
                  "Email" : "thiago@tacs.com",
                  "Pass" : "tacs2024"
                }""";
        Assert.assertThrows(IllegalArgumentException.class,  () ->{
            new UserJSONParser().parseJSONToUser(invalidTestJSON);
        });
    }

}
