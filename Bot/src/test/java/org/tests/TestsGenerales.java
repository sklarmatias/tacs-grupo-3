package org.tests;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.cxf.jaxrs.client.WebClient;
import org.json.JSONArray;
import org.junit.Assert;
import org.junit.Test;
import org.tacsbot.clases.Article;
import org.tacsbot.clases.User;

import java.util.Dictionary;
import java.util.List;

public class TestsGenerales {
    ObjectMapper mapper = new ObjectMapper();
    @Test
    public void artCreationTest() throws Exception {
//        String jsonrequest = "{\n" +
//                "  \"nombre\": \"articulo\",\n" +
//                "  \"imagen\": \"https://th.bing.com/th/id/R.f7447fb4cda31fb43b065d632f87744a?rik=l%2bmSw1SIe%2fU6cw&riu=http%3a%2f%2fupload.wikimedia.org%2fwikipedia%2fcommons%2fb%2fb2%2fToyota_GT86_%e2%80%93_Frontansicht%2c_17._September_2012%2c_D%c3%bcsseldorf.jpg&ehk=YDtN5x6EiONGhyZSC%2b11dAvIFMvFbqpQyJmwN%2fV57YQ%3d&risl=1&pid=ImgRaw&r=0\",\n" +
//                "  \"link\": \"https://www.google.com/\",\n" +
//                "  \"deadline\": \"2024-06-05\",\n" +
//                "  \"usuariosMax\": 6,\n" +
//                "  \"usuariosMin\": 4,\n" +
//                "  \"costo\": 2340000,\n" +
//                "  \"tipoCosto\": \"A\",\n" +
//                "  \"recibe\": \"1 auto\",\n" +
//                "  \"usuariocreado\":1\n" +
//                "}";
//        String url = "https://localhost:7263";
//        WebClient client = WebClient.create("https://localhost:7263/Articulo");
//        Response r = client.type("application/json").post(jsonrequest);
//        Assert.assertEquals(r.getStatus(), 201);
        String jsonrequest = "{\n" +
                "    \"email\":\"asasad@fmsaof.com\",\n" +
                "    \"pass\": \"1234\"\n" +
                "}";
        WebClient client = WebClient.create("http://localhost:8080/restapp/users/login");
        System.out.println(jsonrequest);
        Response response = client.type("application/json").post(jsonrequest);
        String userString = response.readEntity(String.class);
        ObjectMapper mapper = new ObjectMapper();
        User user = mapper.readValue(userString,User.class);
        Assert.assertEquals(response.getStatus(),200);
    }
}