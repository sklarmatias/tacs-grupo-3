package TACS.TACS.reportes;


import TACS.TACS.articulos.Articulo;
import TACS.TACS.articulos.repositorios.RepositorioDeArticulos;
import TACS.TACS.articulos.repositorios.RepositorioDeArticulosEnMemoria;
import TACS.TACS.usuarios.Usuario;
import TACS.TACS.usuarios.repositorios.RepositorioDeUsuarios;
import TACS.TACS.usuarios.repositorios.RepositorioDeUsuariosEnMemoria;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/reportes")
public class ReporteServicio {
	private RepositorioDeUsuarios repo = new RepositorioDeUsuariosEnMemoria();
    private RepositorioDeArticulos repositorioDeArticulos = new RepositorioDeArticulosEnMemoria();

    @GET
    @Path("/total/usuarios")
    public int getTotalUsuarios() {
        return repo.listarUsuarios().size();
    }
    
    @GET
    @Path("/total/articulos")
    public int getTotalArticulos() {
        return repositorioDeArticulos.listarArticulos().size();
    }
    
    @GET
    @Path("/total/success")
    public int getTotalArticuloExito() {
        return repositorioDeArticulos.listarArticulos().stream().filter(Articulo::fueCerradoConExito).toList().size();
    }
    
    @GET
    @Path("/total/fail")
    public int getTotalArticulosFallo() {
        return repositorioDeArticulos.listarArticulos().stream().filter(articulo -> !articulo.fueCerradoConExito()).toList().size();
    }

    @GET
    @Path("/usuarios")
    public int getUsuarios() {
        return repo.listarUsuarios().stream().filter(Usuario::interactuo).toList().size();
    }
}
