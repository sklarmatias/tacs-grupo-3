package TACS.TACS.servicios;

import java.util.List;

import TACS.TACS.anotaciones.Anotacion;
import TACS.TACS.articulos.Articulo;
import TACS.TACS.repositorios.articulos.RepositorioDeArticulos;
import TACS.TACS.repositorios.articulos.RepositorioDeArticulosEnMemoria;
import TACS.TACS.repositorios.usuarios.RepositorioDeUsuarios;
import TACS.TACS.repositorios.usuarios.RepositorioDeUsuariosEnMemoria;
import jakarta.ws.rs.*;

	@Path("/articulos")
public class ArticuloServicio {
		private RepositorioDeArticulos repo = new RepositorioDeArticulosEnMemoria();
		private RepositorioDeUsuarios repoUsuarios = new RepositorioDeUsuariosEnMemoria();

		
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
		@Path("/{id}")
	    @Consumes("application/json")
	    public void updateArticulo(@PathParam("id") int id, Articulo articulo) {
	    	repo.actualizarArticulo(id,articulo);
	    }

	    @POST
	    @Consumes("application/json")
		public Integer addArticulo(Articulo articulo){
			return repo.guardarArticulo(articulo);
		}

	    @POST
	    @Path("/{id}/usuarios")
	    @Consumes("application/json")
	    public void addUsuarioEnArticulo(@PathParam("id") int id,Anotacion anotacion) {
			try {
				Articulo articulo = repo.obtenerArticulo(id);
				articulo.agregarAnotacion(anotacion);
				repo.actualizarArticulo(id, articulo);
			}catch (Exception ex){
				throw ex;
			}
	    }
	    
	    @GET
	    @Path("/{id}/usuarios")
	    @Produces("application/json")
	    public List<Anotacion> getUsuariosEnArticulo(@PathParam("id") int id) {
	    	Articulo articulo = repo.obtenerArticulo(id);
	        return articulo.getAnotaciones();
	    }

		@DELETE
		public void cleanArticulo(){
			repo.borrarArticulos();
		}
}
