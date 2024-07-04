package ar.edu.utn.frba.tacs.controller;

import java.util.List;
import java.util.stream.Collectors;

import ar.edu.utn.frba.tacs.exception.DuplicatedEmailException;
import ar.edu.utn.frba.tacs.model.Client;
import ar.edu.utn.frba.tacs.model.LoggedUser;
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
            return Response.status(201).build();
        } catch (DuplicatedEmailException e){
            return Response.status(400).entity(e.getMessage()).build();
        }
    }
    @POST
    @Path("/login")
    @Consumes("application/json")
    public Response loginUser(User user,@HeaderParam("client") Client client) {
        try{
            LoggedUser.LoggedUserDTO loggedUser = userService.loginUser(user.getEmail(),user.getPass(),client);

            return Response.ok(loggedUser).build();
        }
        catch (LoginException ex){
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

    }
    @GET
    @Path("/session")
    public Response getSessions(@HeaderParam("session") String sessionId){
        if(sessionId != null){
            try{
                return Response.ok(userService.listUserSessions(sessionId)).build();
            }
            catch(Exception ex){
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
        }
        else{
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }
    @DELETE
    @Path("/sessions")
    public Response closeAllSessions(@HeaderParam("session") String sessionId){
        if(sessionId != null){
            try{
                userService.closeAllUserSessions(sessionId);
                return Response.ok().build();
            }
            catch(Exception ex){
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
        }
        else{
            return  Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }
    @DELETE
    @Path("/session")
    public Response closeSession(@HeaderParam("session") String sessionId){
        if(sessionId != null){
            try{
                userService.closeUserSession(sessionId);
                return Response.ok().build();
            }
            catch(Exception ex){
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }

        }
        else{
            return  Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }
}


