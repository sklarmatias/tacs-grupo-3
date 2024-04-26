package tacsWSREST.tacsWSREST;
import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.cxf.jaxrs.client.WebClient;
import org.junit.Assert;
import org.junit.Test;

import TACS.TACS.usuarios.Usuario;
import jakarta.ws.rs.core.Response;

public class UsuarioTest {
	ObjectMapper mapper = new ObjectMapper();
	@Test
    public void testCreacionUsuario() throws Exception {
		//Creo el cliente
		WebClient client = WebClient.create("http://localhost:8080/tacsWSREST/usuarios/");

		//Vacio la base
		Response r = client.delete();
		assertEquals(Response.Status.NO_CONTENT.getStatusCode(), r.getStatus());

		//Creo un usuario de prueba
		Usuario user = new Usuario("John","Doe","john.doe@example.com");

		//Inserto el usuario
		r = client.type("application/json").post(mapper.writeValueAsString(user));
		assertEquals(Response.Status.OK.getStatusCode(), r.getStatus());
		assertEquals(r.readEntity(String.class), "1");

		//Obtengo el usuario
		r = client.path("/1/").accept("application/json").get();
		assertEquals(Response.Status.OK.getStatusCode(), r.getStatus());
		String stringObtenido = r.readEntity(String.class);
		Usuario usuarioObtenido = mapper.readValue(stringObtenido, Usuario.class);

		//Valido los valores
		Assert.assertEquals(user.getNombre(), usuarioObtenido.getNombre());
		Assert.assertEquals(user.getApellido(), usuarioObtenido.getApellido());
		Assert.assertEquals(user.getMail(), usuarioObtenido.getMail());
		client.close();
    }
}
