package ar.edu.utn.frba.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

public class GeneralTests {
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
//        WebClient client = WebClient.create("http://localhost:8080/restapp/articles/1/users/1");
//        Response response = client.type("application/json").post("");
//
//        Assert.assertEquals(response.getStatus(),200);
    }
}