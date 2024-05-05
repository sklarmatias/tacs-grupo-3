package ar.edu.utn.frba.tacs;

import static org.junit.Assert.assertEquals;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.cxf.jaxrs.client.WebClient;
import org.junit.Assert;
import org.junit.Test;
import ar.edu.utn.frba.tacs.model.User;
import jakarta.ws.rs.core.Response;

public class UserTest {
//	ObjectMapper mapper = new ObjectMapper();
//	@Test
//    public void userCreationTest() throws Exception {
//		// TODO use env variable
//		String url = "http://localhost:8080/tacsWSREST";
//		WebClient client = WebClient.create(String.format("%s/users", url));
//
//		Response r = client.delete();
//		assertEquals(Response.Status.NO_CONTENT.getStatusCode(), r.getStatus());
//
//		//create test user
//		User user = new User("John","Doe","john.doe@example.com");
//
//		//Save user
//		r = client.type("application/json").post(mapper.writeValueAsString(user));
//		assertEquals(Response.Status.CREATED.getStatusCode(), r.getStatus());
//		assertEquals("/tacsWSREST/users/1", r.getLocation().getPath());
//
//		//Get user
//		r = client.path("1").accept("application/json").get();
//		assertEquals(Response.Status.OK.getStatusCode(), r.getStatus());
//		String responseStr = r.readEntity(String.class);
//		User.UserDTO responseUser = mapper.readValue(responseStr, User.UserDTO.class);
//
//		//Validate
//		Assert.assertEquals(user.getName(), responseUser.name);
//		Assert.assertEquals(user.getSurname(), responseUser.surname);
//		Assert.assertEquals(user.getEmail(), responseUser.email);
//		client.close();
//    }
}
