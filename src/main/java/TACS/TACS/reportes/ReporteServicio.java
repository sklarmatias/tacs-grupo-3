package TACS.TACS.reportes;


import TACS.TACS.articulos.Articulo;
import TACS.TACS.articulos.repositorios.RepositorioDeArticulos;
import TACS.TACS.articulos.repositorios.RepositorioDeArticulosEnMemoria;
import TACS.TACS.usuarios.Usuario;
import TACS.TACS.usuarios.repositorios.RepositorioDeUsuarios;
import TACS.TACS.usuarios.repositorios.RepositorioDeUsuariosEnMemoria;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import java.util.stream.Collectors;

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
        return (int) repositorioDeArticulos.listarArticulos().stream().filter(Articulo::fueCerradoConExito).count();
    }
    
    @GET
    @Path("/total/fail")
    public int getTotalArticulosFallo() {
        return (int) repositorioDeArticulos.listarArticulos().stream().filter(articulo -> !articulo.fueCerradoConExito()).count();
    }

    @GET
    @Path("/usuarios")
    public int getUsuarios() {
        return (int) repo.listarUsuarios().stream().filter(Usuario::interactuo).count();
    }
}
