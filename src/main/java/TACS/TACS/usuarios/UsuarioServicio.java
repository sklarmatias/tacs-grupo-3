package TACS.TACS.usuarios;

import java.util.List;
import TACS.TACS.usuarios.repositorios.RepositorioDeUsuarios;
import TACS.TACS.usuarios.repositorios.RepositorioDeUsuariosEnMemoria;
import jakarta.ws.rs.*;

@Path("/usuarios")
@Produces("application/json")
public class UsuarioServicio {
    private RepositorioDeUsuarios repo = new RepositorioDeUsuariosEnMemoria();
    @GET
    public List<Usuario.UsuarioDTO> getUsuarios() {
        return repo.listarUsuarios().stream().map(Usuario::convertirADTO).toList();
    }
    @GET
    @Path("/{id}")
    public Usuario.UsuarioDTO getUsuario(@PathParam("id") int id) {
        return repo.obtenerUsuario(id).convertirADTO();
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


