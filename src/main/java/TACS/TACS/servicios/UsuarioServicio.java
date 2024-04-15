package TACS.TACS.servicios;

import java.util.ArrayList;
import java.util.List;

import TACS.TACS.repositorios.usuarios.RepositorioDeUsuariosEnMemoria;
import TACS.TACS.usuarios.Usuario;
import jakarta.ws.rs.*;
@Path("/usuarios")
public class UsuarioServicio {
    private RepositorioDeUsuariosEnMemoria repo = new RepositorioDeUsuariosEnMemoria();

    @GET
    @Produces("application/json")
    public List<Usuario> getUsuarios() {
        return repo.listarUsuarios();
    }
    @GET
    @Path("/{id}")
    @Produces("application/json")
    public Usuario getUsuario(@PathParam("id") int id) {
        return repo.obtenerUsuario(id);
    }
    @PATCH
    @Consumes("application/json")
    public void updateUsuario(Usuario usuario) {
    	repo.actualizarUsuario(usuario);
    }
    @POST
    @Consumes("application/json")
    public void addUsuario(Usuario usuario) {
        repo.guardarUsuario(usuario);
    }

}


