package TACS.TACS.servicios;


import TACS.TACS.repositorios.usuarios.RepositorioDeUsuariosEnMemoria;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/reportes")
public class ReporteServicio {
	private RepositorioDeUsuariosEnMemoria repo = new RepositorioDeUsuariosEnMemoria();

    @GET
    @Path("/total/usuarios")
    public int getTotalUsuarios() {
    	//TO DO: contador
        return 0;
    }
    
    @GET
    @Path("/total/articulos")
    public int getTotalArticulos() {
    	//TO DO: contador
        return 0;
    }
    
    @GET
    @Path("/total/success")
    public int getTotalArticuloExito() {
    	//TO DO: contador
        return 0;
    }
    
    @GET
    @Path("/total/fail")
    public int getTotalArticulosFallo() {
    	//TO DO: contador
        return 0;
    }
    
    @GET
    @Path("/usuarios")
    public int getUsuarios() {
    	//TO DO: 
        return 0;
    }
}
