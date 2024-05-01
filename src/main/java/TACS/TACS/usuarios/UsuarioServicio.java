package TACS.TACS.usuarios;

import java.util.List;
import java.util.stream.Collectors;
import TACS.TACS.usuarios.repositorios.RepositorioDeUsuarios;
import TACS.TACS.usuarios.repositorios.RepositorioDeUsuariosEnMemoria;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;

@Path("/usuarios")
@Produces("application/json")
public class UsuarioServicio {
    private RepositorioDeUsuarios repo = new RepositorioDeUsuariosEnMemoria();
    @GET
    public List<Usuario.UsuarioDTO> getUsuarios() {
        return repo.listarUsuarios().stream().map(Usuario::convertirADTO).collect(Collectors.toList());
    }
    @GET
    @Path("/{id}")
    public Usuario.UsuarioDTO getUsuario(@PathParam("id") int id) {
        return repo.obtenerUsuario(id).convertirADTO();
    }

    // devuelve un 204
    @PATCH
    @Path("/{id}")
    @Consumes("application/json")
    public void updateUsuario(@PathParam("id") int id, Usuario usuario) {
        repo.actualizarUsuario(id, usuario);
    }

    // devuelve un 201
    // en el Header, devuelve un header Location que contiene la URL de obtención del recurso
    @POST
    @Consumes("application/json")
    public Response addUsuario(Usuario usuario,@Context UriInfo uriInfo) {
        int idUsuario = repo.guardarUsuario(new Usuario(usuario.getNombre(), usuario.getApellido(), usuario.getMail()));
        // creo la URI con la que se obtendría el recurso creado
        UriBuilder usuarioURIBuilder = uriInfo.getAbsolutePathBuilder();
        usuarioURIBuilder.path(Integer.toString(idUsuario));
        return Response.created(usuarioURIBuilder.build()).build();
    }

    // TODO eliminar este path, no lo vamos a usar más que en los tests. Refactorizar esa parte.
    @DELETE
    public void cleanUsuarios(){
        repo.borrarUsuarios();
    }
}


