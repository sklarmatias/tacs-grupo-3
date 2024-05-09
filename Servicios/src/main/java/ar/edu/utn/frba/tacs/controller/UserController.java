package ar.edu.utn.frba.tacs.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import ar.edu.utn.frba.tacs.model.User;
import ar.edu.utn.frba.tacs.repository.MongoDBConnector;
import ar.edu.utn.frba.tacs.service.UserService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import org.bson.Document;

@Path("/users")
@Produces("application/json")
public class UserController {

//    private final UserService userService = new UserService();
    MongoDBConnector dbConnector = new MongoDBConnector();
    @GET
    public List<User.UserDTO> listUsers() {
//        return userService.listUsers().stream().map(User::convertToDTO).collect(Collectors.toList());
        List<Document> documents = dbConnector.selectAll("users");
        return documents.stream().map(document -> dbConnector.DocumentToUser(document)).toList();
    }
    @GET
    @Path("/{id}")
    public User.UserDTO getUser(@PathParam("id") String id) {
        Document document = dbConnector.selectById("users",id);
        return dbConnector.DocumentToUser(document);
//        return userService.getUser(id).convertToDTO();
    }

    // 204
    @PATCH
    @Path("/{id}")
    @Consumes("application/json")
    public void updateUser(@PathParam("id") String id, User user) {

//        userService.updateUser(id, user);
    }

    // 201
    // Location header -> get URL
    @POST
    @Consumes("application/json")
    public Response saveUser(User user, @Context UriInfo uriInfo) {
//        int userId = userService.saveUser(user.getName(), user.getSurname(), user.getEmail(),user.getPass());
        String userId = dbConnector.insert(user.toDocument(),"users");
        // URI
        return Response.ok().build();
    }
    @POST
    @Path("/login")
    @Consumes("application/json")
    public User.UserDTO loginUser(User user, @Context UriInfo uriInfo) {
//        return userService.loginUser(user.getEmail(),user.getPass()).convertToDTO();
        Map<String,Object> conditions = new HashMap<>();
        conditions.put("email",user.getEmail());
        conditions.put("pass",user.getPass());
        return dbConnector.DocumentToUser(dbConnector.selectByCondition("users",conditions).getFirst());
    }

    // TODO delete this method
    @DELETE
    public void cleanUsers(){

//        userService.cleanUsers();
    }
}


