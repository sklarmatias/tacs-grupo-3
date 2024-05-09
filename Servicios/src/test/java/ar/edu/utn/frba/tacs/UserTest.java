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
    public void testAddUsuarioLocal() {
        MongoDBConnector dbConnector = new MongoDBConnector();
        User user = new User("juan","perez","jp@gmail.com","123456");
        String id = dbConnector.insert(user.toDocument(),"users");
        User newuser = new User();
        newuser.fromDocument(dbConnector.selectById("users",id));
        assertEquals(id,newuser.getId());
        dbConnector.deleteById("users",id);
        assertNull(dbConnector.selectById("users",id));
        dbConnector.closeConnection();
    }
    @Test
    public void testLogin(){
        MongoDBConnector dbConnector = new MongoDBConnector();
        User user = new User("juan","perez","jp@gmail.com","123456");
        String id = dbConnector.insert(user.toDocument(),"users");
        Map<String,Object> conditions = new HashMap<>();
        conditions.put("email",user.getEmail());
        conditions.put("pass",user.getPass());
        User newuser = new User();
        newuser.fromDocument(dbConnector.selectByCondition("users",conditions).getFirst());
        assertEquals(id, newuser.getId());
        dbConnector.deleteById("users",id);
        assertNull(dbConnector.selectById("users",id));
        dbConnector.closeConnection();
    }
}
