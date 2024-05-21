package ar.edu.utn.frba.tacs.mappers;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

import javax.security.auth.login.LoginException;

public class LoginExceptionMapper implements ExceptionMapper<LoginException> {
    @Override
    public Response toResponse(LoginException exception) {
        return Response.status(Response.Status.UNAUTHORIZED)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity("Wrong username or password.")
                .build();
    }
}
