package tacsWSREST.tacsWSREST;

import TACS.TACS.anotaciones.Anotacion;
import TACS.TACS.articulos.Articulo;
import TACS.TACS.articulos.EstadoArticulo;
import TACS.TACS.articulos.TipoCosto;
import TACS.TACS.usuarios.Usuario;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.Response;
import org.apache.cxf.jaxrs.client.WebClient;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class ArticuloTest {
    ObjectMapper mapper = new ObjectMapper();
        @Test
        public void testCreacion() throws Exception {

            Articulo articulo1 = new Articulo(1,"Articulo1","001.jpg","","1kg", EstadoArticulo.OPEN,new Date(),1,100.00, TipoCosto.POR_PERSONA,1,5);
            WebClient client = WebClient.create("http://localhost:8080/restapp/articulos/");
            Response r = client.type("application/json").post(mapper.writeValueAsString(articulo1));
            assertEquals(Response.Status.NO_CONTENT.getStatusCode(), r.getStatus());
            r = client.path("/1/").accept("application/json").get();
            assertEquals(Response.Status.OK.getStatusCode(), r.getStatus());
            String articuloResponse = r.readEntity(String.class);
            Articulo articulo2 = mapper.readValue(articuloResponse, Articulo.class);
            Assert.assertEquals(articulo1.getId(), articulo2.getId());
            Assert.assertEquals(articulo1.getNombre(), articulo2.getNombre());
            Assert.assertEquals(articulo1.getCosto(), articulo2.getCosto());
        }
        @Test
    public void testAgregarUsuario() throws Exception{
            Articulo articulo1 = new Articulo(2,"Articulo2","002.jpg","","1kg", EstadoArticulo.OPEN,new Date(),1,150.00, TipoCosto.POR_PERSONA,1,5);
            WebClient client = WebClient.create("http://localhost:8080/restapp/articulos/");
            String json =mapper.writeValueAsString(articulo1);
            Response r = client.type("application/json").post(json);
            assertEquals(Response.Status.NO_CONTENT.getStatusCode(), r.getStatus());
            Usuario user = new Usuario(2,"John","Doe","john.doe@example.com");
            Anotacion anotacion = new Anotacion(user);
            r = client.path("/2/usuarios").type("application/json").post(mapper.writeValueAsString(anotacion));
            assertEquals(Response.Status.NO_CONTENT.getStatusCode(), r.getStatus());
            r = client.accept("application/json").get();
            String anotacionesResponse = r.readEntity(String.class);
            List<Anotacion> anotaciones = mapper.readValue(anotacionesResponse, new TypeReference<List<Anotacion>>(){});
            assertFalse(anotaciones.isEmpty());
        }
}
