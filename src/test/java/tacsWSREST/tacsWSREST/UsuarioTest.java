package tacsWSREST.tacsWSREST;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import TACS.TACS.repositorios.usuarios.RepositorioDeUsuariosEnMemoria;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.cxf.jaxrs.client.WebClient;
import org.junit.Assert;
import org.junit.Test;

import TACS.TACS.usuarios.Usuario;
import jakarta.ws.rs.core.Response;

public class UsuarioTest {
	ObjectMapper mapper = new ObjectMapper();
	@Test
    public void testCreacion() throws Exception {
		Usuario user = new Usuario(1,"John","Doe","john.doe@example.com");
		WebClient client = WebClient.create("http://localhost:8080/restapp/usuarios/");
		Response r = client.type("application/json").post(mapper.writeValueAsString(user));
		assertEquals(Response.Status.NO_CONTENT.getStatusCode(), r.getStatus());
		r = client.path("/1/").accept("application/json").get();
		assertEquals(Response.Status.OK.getStatusCode(), r.getStatus());
		String usuarioResponse = r.readEntity(String.class);
		Usuario userclase = mapper.readValue(usuarioResponse, Usuario.class);
		Assert.assertEquals(user.getId(), userclase.getId());
		Assert.assertEquals(user.getNombre(), userclase.getNombre());
		Assert.assertEquals(user.getApellido(), userclase.getApellido());
		Assert.assertEquals(user.getMail(), userclase.getMail());
	}
}
