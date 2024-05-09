package ar.edu.utn.frba.tacs;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class UserTest {
    @Test
    public void testAddUsuario() {
        MongoDBConnector dbConnector = new MongoDBConnector();
        User user = new User("juan","perez","jp@gmail.com","123456");
        User newuser = new User(user.getId());
        assertEquals(user.getId(),newuser.getId());
        dbConnector.deleteById("users",user.getId());
        assertNull(dbConnector.selectById("users",user.getId()));
        dbConnector.closeConnection();
    }
    @Test
    public void testLogin(){
        MongoDBConnector dbConnector = new MongoDBConnector();
        User user = new User("juan","perez","jp@gmail.com","123456");
        User newuser = new User(user.getEmail(),user.getPass());
        assertEquals(user.getId(), newuser.getId());
        dbConnector.deleteById("users",user.getId());
        assertNull(dbConnector.selectById("users",user.getId()));
        dbConnector.closeConnection();
    }
}
