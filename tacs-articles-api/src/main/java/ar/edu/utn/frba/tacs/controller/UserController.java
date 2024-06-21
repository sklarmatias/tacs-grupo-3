package ar.edu.utn.frba.tacs.controller;

import java.util.List;
import java.util.stream.Collectors;

import ar.edu.utn.frba.tacs.exception.DuplicatedEmailException;
import ar.edu.utn.frba.tacs.model.User;
import ar.edu.utn.frba.tacs.service.ArticleService;
import ar.edu.utn.frba.tacs.service.UserService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;

import javax.security.auth.login.LoginException;

@Path("/users")
@Produces("application/json")
public class UserController {
    private final UserService userService;
    public UserController(){
        userService= new UserService(System.getenv("CON_STRING"));
    }
    public UserController(UserService userService){
        this.userService = userService;
    }



    @GET
    public List<User.UserDTO> listUsers() {
        return userService.listUsers().stream().map(User::convertToDTO).collect(Collectors.toList());
    }
    @GET
    @Path("/{id}")
    public User.UserDTO getUser(@PathParam("id") String id) {
        return userService.getUser(id).convertToDTO();
    }

    // 204
    @PATCH
    @Path("/{id}")
    @Consumes("application/json")
    public void updateUser(@PathParam("id") String id, User user) {
        userService.updateUser(id, user);
    }

    // 201
    // Location header -> get URL
    @POST
    @Path("/register")
    @Consumes("application/json")
    public Response saveUser(User user) {
        try{
            String userId = userService.saveUser(user);
            // URI
//            UriBuilder userURIBuilder = uriInfo.getBaseUriBuilder().path("/users");
//            userURIBuilder.path(userId);
//            return Response.created(userURIBuilder.build()).build();
            return Response.status(201).build();
        } catch (DuplicatedEmailException e){
            return Response.status(400).entity(e.getMessage()).build();
        }
    }
    @POST
    @Path("/login")
    @Consumes("application/json")
    public User.UserDTO loginUser(User user) throws LoginException {
        return userService.loginUser(user.getEmail(),user.getPass()).convertToDTO();
    }
    public void delete(String id){
        userService.delete(id);
    }
}


