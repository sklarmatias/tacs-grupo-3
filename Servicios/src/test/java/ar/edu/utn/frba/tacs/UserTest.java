package ar.edu.utn.frba.tacs;

import static org.junit.Assert.assertEquals;

import ar.edu.utn.frba.tacs.repository.MongoDBConnector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.apache.cxf.jaxrs.client.WebClient;
import org.bson.Document;
import org.bson.json.JsonWriterSettings;
import org.junit.Assert;
import org.junit.Test;
import ar.edu.utn.frba.tacs.model.User;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;

public class UserTest {
    @Test
    public void testAddUsuarioLocal() {
        MongoDBConnector dbConnector = new MongoDBConnector();
        User user = new User("juan","perez","jp@gmail.com","123456");
        dbConnector.insert(user.toDocument(),"users");
        User newuser = new User();
        newuser.fromDocument(dbConnector.select("users"));
        assertEquals(user.getEmail(),newuser.getEmail());
    }
}
