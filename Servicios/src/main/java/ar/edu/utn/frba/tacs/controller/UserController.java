package ar.edu.utn.frba.tacs.controller;

import java.util.List;
import java.util.stream.Collectors;
import ar.edu.utn.frba.tacs.repository.user.UsersRepository;
import ar.edu.utn.frba.tacs.repository.user.impl.InMemoryUsersRepository;
import ar.edu.utn.frba.tacs.model.User;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;

@Path("/users")
@Produces("application/json")
public class UserController {
    private final UsersRepository usersRepository = new InMemoryUsersRepository();
    @GET
    public List<User.UserDTO> listUsers() {
        return usersRepository.findAll().stream().map(User::convertToDTO).collect(Collectors.toList());
    }
    @GET
    @Path("/{id}")
    public User.UserDTO getUser(@PathParam("id") int id) {
        return usersRepository.find(id).convertToDTO();
    }

    // 204
    @PATCH
    @Path("/{id}")
    @Consumes("application/json")
    public void updateUser(@PathParam("id") int id, User user) {
        usersRepository.update(id, user);
    }

    // 201
    // Location header -> get URL
    @POST
    @Consumes("application/json")
    public Response saveUser(User user, @Context UriInfo uriInfo) {
        int userId = usersRepository.save(new User(user.getName(), user.getSurname(), user.getEmail()));
        // URI
        UriBuilder userURIBuilder = uriInfo.getAbsolutePathBuilder();
        userURIBuilder.path(Integer.toString(userId));
        return Response.created(userURIBuilder.build()).build();
    }

    // TODO delete this method
    @DELETE
    public void cleanArticles(){
        usersRepository.delete();
    }
}


