package ar.edu.utn.frba.tacs.controller;

import java.util.List;
import java.util.stream.Collectors;
import ar.edu.utn.frba.tacs.model.User;
import ar.edu.utn.frba.tacs.service.UserService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;

@Path("/users")
@Produces("application/json")
public class UserController {

    private final UserService userService = new UserService();

    @GET
    public List<User.UserDTO> listUsers() {
        return userService.listUsers().stream().map(User::convertToDTO).collect(Collectors.toList());
    }
    @GET
    @Path("/{id}")
    public User.UserDTO getUser(@PathParam("id") int id) {
        return userService.getUser(id).convertToDTO();
    }

    // 204
    @PATCH
    @Path("/{id}")
    @Consumes("application/json")
    public void updateUser(@PathParam("id") int id, User user) {
        userService.updateUser(id, user);
    }

    // 201
    // Location header -> get URL
    @POST
    @Consumes("application/json")
    public Response saveUser(User user, @Context UriInfo uriInfo) {
        int userId = userService.saveUser(user.getName(), user.getSurname(), user.getEmail());
        // URI
        UriBuilder userURIBuilder = uriInfo.getAbsolutePathBuilder();
        userURIBuilder.path(Integer.toString(userId));
        return Response.created(userURIBuilder.build()).build();
    }

    // TODO delete this method
    @DELETE
    public void cleanUsers(){
        userService.cleanUsers();
    }
}


