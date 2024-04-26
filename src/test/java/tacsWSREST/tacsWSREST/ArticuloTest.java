package tacsWSREST.tacsWSREST;

import TACS.TACS.anotaciones.Anotacion;
import TACS.TACS.articulos.Articulo;
import TACS.TACS.articulos.EstadoArticulo;
import TACS.TACS.articulos.TipoCosto;
import TACS.TACS.articulos.repositorios.RepositorioDeArticulos;
import TACS.TACS.articulos.repositorios.RepositorioDeArticulosEnMemoria;
import TACS.TACS.usuarios.repositorios.RepositorioDeUsuarios;
import TACS.TACS.usuarios.repositorios.RepositorioDeUsuariosEnMemoria;
import TACS.TACS.usuarios.Usuario;
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
    RepositorioDeUsuarios repous = new RepositorioDeUsuariosEnMemoria();
    RepositorioDeArticulos repoar = new RepositorioDeArticulosEnMemoria();
    @Test
    public void testAddUsuarioLocal(){
        repous.borrarUsuarios();
        repoar.borrarArticulos();
        Usuario user = new Usuario("John","Doe","john.doe@example.com");
        repous.guardarUsuario(user);
        Articulo articulo1 = new Articulo("Articulo1","001.jpg","","1kg", EstadoArticulo.OPEN,new Date(),user,100.00, TipoCosto.POR_PERSONA,1,5);
        repoar.guardarArticulo(articulo1);
        Anotacion anotacion = new Anotacion(repous.obtenerUsuario(1));
        Articulo articulo = repoar.obtenerArticulo(1);
        articulo.agregarAnotacion(anotacion);
        repoar.actualizarArticulo(1,articulo);
        Articulo art1 = repoar.obtenerArticulo(1);
        assertEquals(1,art1.getAnotaciones().get(0).getUsuario().getId().intValue());
    }


        @Test
        public void testCreacion() throws Exception {
            //Creo el cliente
            WebClient client = WebClient.create("http://localhost:8080/restapp/usuarios/");

            //Vacio la base
            Response r = client.delete();
            assertEquals(Response.Status.NO_CONTENT.getStatusCode(), r.getStatus());

            //Creo un usuario de prueba
            Usuario user = new Usuario("John","Doe","john.doe@example.com");

            //Inserto el usuario
            r = client.type("application/json").post(mapper.writeValueAsString(user));
            assertEquals(Response.Status.NO_CONTENT.getStatusCode(), r.getStatus());
            repous.guardarUsuario(user);

            client = WebClient.create("http://localhost:8080/restapp/articulos/");
            //Vacio la base
            r = client.delete();
            assertEquals(Response.Status.NO_CONTENT.getStatusCode(), r.getStatus());

            //Creo un articulo de prueba
            Articulo articulo1 = new Articulo("Articulo1","001.jpg","","1kg", EstadoArticulo.OPEN,new Date(),user,100.00, TipoCosto.POR_PERSONA,1,5);

            //Inserto en la base
            r = client.type("application/json").post(mapper.writeValueAsString(articulo1));
            assertEquals(Response.Status.NO_CONTENT.getStatusCode(), r.getStatus());

            //Obtengo el articulo
            r = client.path("/1/").accept("application/json").get();
            assertEquals(Response.Status.OK.getStatusCode(), r.getStatus());
            String articuloResponse = r.readEntity(String.class);
            Articulo articulo2 = mapper.readValue(articuloResponse, Articulo.class);

            //Valido los valores
            Assert.assertEquals(articulo1.getNombre(), articulo2.getNombre());
            Assert.assertEquals(articulo1.getCosto(), articulo2.getCosto());
        }
        @Test
    public void testAgregarUsuario() throws Exception{
        repoar.borrarArticulos();
        repous.borrarUsuarios();
            //Creo el cliente
            WebClient client = WebClient.create("http://localhost:8080/restapp/usuarios/");

            //Vacio la base
            Response r = client.delete();
            assertEquals(Response.Status.NO_CONTENT.getStatusCode(), r.getStatus());

            //Creo un usuario de prueba
            Usuario user = new Usuario("John","Doe","john.doe@example.com");

            //Inserto el usuario
            r = client.type("application/json").post(mapper.writeValueAsString(user));
            assertEquals(Response.Status.NO_CONTENT.getStatusCode(), r.getStatus());
            repous.guardarUsuario(user);

            //Creo el cliente
            client = WebClient.create("http://localhost:8080/restapp/articulos/");

            //Vacio la base
            r = client.delete();
            assertEquals(Response.Status.NO_CONTENT.getStatusCode(), r.getStatus());

            //Creo un articulo de prueba
            Articulo articulo1 = new Articulo("Articulo2","002.jpg","","1kg", EstadoArticulo.OPEN,new Date(),user,150.00, TipoCosto.POR_PERSONA,1,5);

            //Inserto en la base
            r = client.type("application/json").post(mapper.writeValueAsString(articulo1));
            assertEquals(Response.Status.NO_CONTENT.getStatusCode(), r.getStatus());

            //Creo una anotacion
            Anotacion anotacion = new Anotacion(repous.obtenerUsuario(1));

            //Inserto la anotacion
            r = client.path("/1/usuarios").type("application/json").post(mapper.writeValueAsString(anotacion));
            assertEquals(Response.Status.NO_CONTENT.getStatusCode(), r.getStatus());

            //Obtengo lista de anotaciones
            r = client.accept("application/json").get();
            String anotacionesResponse = r.readEntity(String.class);
            List<Anotacion> anotaciones = mapper.readValue(anotacionesResponse, new TypeReference<List<Anotacion>>(){});
            assertFalse(anotaciones.isEmpty());
            assertEquals(1,anotaciones.get(0).getUsuario().getId().intValue());
        }

}
