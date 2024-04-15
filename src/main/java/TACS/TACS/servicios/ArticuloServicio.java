package TACS.TACS.servicios;

import java.util.List;

import TACS.TACS.anotaciones.Anotacion;
import TACS.TACS.articulos.Articulo;
import TACS.TACS.repositorios.articulos.RepositorioDeArticulosEnMemoria;
import jakarta.ws.rs.*;

	@Path("/articulos")
public class ArticuloServicio {
		private RepositorioDeArticulosEnMemoria repo = new RepositorioDeArticulosEnMemoria();
		
		@GET
	    @Produces("application/json")
	    public List<Articulo> getArticulos() {
	        return repo.listarArticulos();
	    }
		
	    @GET
	    @Path("/{id}")
	    @Produces("application/json")
	    public Articulo getArticulo(@PathParam("id") int id) {
	        return repo.obtenerArticulo(id);
	    }
	    
	    @PATCH
	    @Consumes("application/json")
	    public void updateArticulo(Articulo articulo) {
	    	repo.actualizarArticulo(articulo);
	    }
	    
	    @POST
	    @Consumes("application/json")
	    public void addArticulo(Articulo articulo) {
	        repo.guardarArticulo(articulo);
	    }
	    
	    @POST
	    @Path("/{id}/usuarios")
	    @Consumes("application/json")
	    public void addUsuarioEnArticulo(@PathParam("id") int id,Anotacion anotacion) {
	        Articulo articulo = repo.obtenerArticulo(id);
	        articulo.agregarAnotacion(anotacion);
	        repo.actualizarArticulo(articulo);
	    }
	    
	    @GET
	    @Path("/{id}/usuarios")
	    @Produces("application/json")
	    public List<Anotacion> getUsuariosEnArticulo(@PathParam("id") int id) {
	    	Articulo articulo = repo.obtenerArticulo(id);
	        return articulo.getAnotaciones();
	    }
}
