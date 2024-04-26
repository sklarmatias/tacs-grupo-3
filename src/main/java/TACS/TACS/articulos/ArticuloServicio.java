package TACS.TACS.articulos;

import java.util.List;

import TACS.TACS.anotaciones.Anotacion;
import TACS.TACS.anotaciones.repositorio.RepositorioDeAnotaciones;
import TACS.TACS.anotaciones.repositorio.RepositorioDeAnotacionesEnMemoria;
import TACS.TACS.articulos.repositorios.RepositorioDeArticulos;
import TACS.TACS.articulos.repositorios.RepositorioDeArticulosEnMemoria;
import TACS.TACS.usuarios.Usuario;
import TACS.TACS.usuarios.repositorios.RepositorioDeUsuarios;
import TACS.TACS.usuarios.repositorios.RepositorioDeUsuariosEnMemoria;
import jakarta.ws.rs.*;

	@Path("/articulos")
public class ArticuloServicio {
		private RepositorioDeArticulos repo = new RepositorioDeArticulosEnMemoria();
		private RepositorioDeUsuarios repoUsuarios = new RepositorioDeUsuariosEnMemoria();

		private RepositorioDeAnotaciones repositorioDeAnotaciones = new RepositorioDeAnotacionesEnMemoria();

		
		@GET
	    @Produces("application/json")
	    public List<Articulo.ArticuloDTO> getArticulos() {
	        return repo.listarArticulos().stream().map(Articulo::convertirADTO).toList();
	    }
		
	    @GET
	    @Path("/{id}")
	    @Produces("application/json")
	    public Articulo.ArticuloDTO getArticulo(@PathParam("id") int id) {
	        return repo.obtenerArticulo(id).convertirADTO();
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
			Usuario usuario = repoUsuarios.obtenerUsuario(articulo.getPropietario().getId());
			usuario.getArticulosPublicados().add(articulo);
			return repo.guardarArticulo(
					new Articulo(
							articulo.getNombre(),
							articulo.getImagen(),
							articulo.getLink(),
							articulo.getUsuarioRecibe(),
							repoUsuarios.obtenerUsuario(articulo.getPropietario().getId()),
							articulo.getDeadline(),
							articulo.getCosto(),
							articulo.getTipoCosto(),
							articulo.getMinimoUsuarios(),
							articulo.getMaximoUsuarios()));
		}

	    @POST
	    @Path("/{id}/usuarios")
	    @Consumes("application/json")
	    public void addUsuarioEnArticulo(@PathParam("id") int id,Anotacion anotacion) {
			Usuario usuarioAnotado = repoUsuarios.obtenerUsuario(anotacion.getUsuario().getId());
			Anotacion anotacion1 = new Anotacion(usuarioAnotado);
			repositorioDeAnotaciones.guardarAnotacion(anotacion1);
			usuarioAnotado.getAnotaciones().add(anotacion1);
			repoUsuarios.guardarUsuario(usuarioAnotado);
			Articulo articulo = repo.obtenerArticulo(id);
			articulo.agregarAnotacion(anotacion1);
			repo.actualizarArticulo(id, articulo);
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
