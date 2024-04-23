package TACS.TACS.servicios;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import TACS.TACS.repositorios.usuarios.RepositorioDeUsuarios;
import TACS.TACS.repositorios.usuarios.RepositorioDeUsuariosEnMemoria;
import TACS.TACS.usuarios.Usuario;
import jakarta.ws.rs.*;

@Path("/usuarios")
public class UsuarioServicio {
    private RepositorioDeUsuarios repo = new RepositorioDeUsuariosEnMemoria();

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
    @Path("/{id}")
    @Consumes("application/json")
    public void updateUsuario(@PathParam("id") int id, Usuario usuario) {
        if (usuario.getNombre() == null)
            throw new IllegalArgumentException("El valor del campo \"nombre\" es obligatorio");
        if (usuario.getApellido() == null)
            throw new IllegalArgumentException("El valor del campo \"apellido\" es obligatorio");
        if (usuario.getMail() == null)
            throw new IllegalArgumentException("El valor del campo \"email\" es obligatorio");
    	repo.actualizarUsuario(id,usuario);
    }
    @POST
    @Consumes("application/json")
    public Integer addUsuario(Usuario usuario) {
        if (usuario.getNombre() == null)
            throw new IllegalArgumentException("El valor del campo \"nombre\" es obligatorio");
        if (usuario.getApellido() == null)
            throw new IllegalArgumentException("El valor del campo \"apellido\" es obligatorio");
        if (usuario.getMail() == null)
            throw new IllegalArgumentException("El valor del campo \"email\" es obligatorio");
        return repo.guardarUsuario(new Usuario(usuario.getNombre(), usuario.getApellido(), usuario.getMail()));
    }

    // TODO eliminar este path, no lo vamos a usar más que en los tests. Refactorizar esa parte.
    @DELETE
    public void cleanUsuarios(){
        repo.borrarUsuarios();
    }
}


