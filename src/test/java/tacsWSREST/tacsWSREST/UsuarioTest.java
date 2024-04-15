package tacsWSREST.tacsWSREST;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.apache.cxf.jaxrs.client.WebClient;
import org.junit.Assert;
import org.junit.Test;

import TACS.TACS.usuarios.Usuario;
import jakarta.ws.rs.core.Response;

public class UsuarioTest {
	@Test
    public void testCreacion() throws Exception {
		String usuarioJson = "{\"id\":1,\"nombre\":\"John\",\"apellido\":\"Doe\",\"mail\":\"john.doe@example.com\",\"articulosPublicados\":null,\"anotaciones\":null}";
		WebClient client = WebClient.create("http://localhost:8080/restapp/usuarios/");
		Response r = client.type("application/json").post(usuarioJson);
		assertEquals(Response.Status.NO_CONTENT.getStatusCode(), r.getStatus());
		r = client.path("/1/").accept("application/json").get();
		assertEquals(Response.Status.OK.getStatusCode(), r.getStatus());
		String usuarioResponse = r.readEntity(String.class);
		
		Assert.assertEquals(usuarioResponse, usuarioJson);
	}
}
