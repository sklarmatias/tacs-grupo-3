package TACS.TACS.servicios;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import TACS.TACS.anotaciones.Anotacion;
import TACS.TACS.articulos.Articulo;
import TACS.TACS.articulos.TipoCosto;
import TACS.TACS.repositorios.articulos.RepositorioDeArticulos;
import TACS.TACS.repositorios.articulos.RepositorioDeArticulosEnMemoria;
import TACS.TACS.repositorios.usuarios.RepositorioDeUsuarios;
import TACS.TACS.repositorios.usuarios.RepositorioDeUsuariosEnMemoria;
import TACS.TACS.usuarios.Usuario;
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
	    public Integer addArticulo(@QueryParam("nombre") String nombre,
								@QueryParam("imagen") String imagen,
								@QueryParam("link") String link,
								@QueryParam("usuario_recibe") String usuarioRecibe,
								@QueryParam("deadline") Date deadline,
								@QueryParam("id_usuario") Integer idUsuario,
								@QueryParam("costo") Double costo,
								@QueryParam("tipoCosto") String tipoCosto,
								@QueryParam("min_usuarios") Integer minUsuarios,
								@QueryParam("max_usuarios") Integer maxUsuarios
								) {
			TipoCosto tipoCosto1;
			if (Objects.equals(tipoCosto, "TOTAL"))
				tipoCosto1 = TipoCosto.TOTAL;
			else if (Objects.equals(tipoCosto, "POR_PERSONA"))
				tipoCosto1 = TipoCosto.POR_PERSONA;
			else{
				throw new IllegalArgumentException(String.format("Tipo de costo \"%s\"erróneo.", tipoCosto));
			}
			Usuario usuario = repoUsuarios.obtenerUsuario(idUsuario);
			Articulo articulo = new Articulo(nombre, imagen, link, usuarioRecibe, usuario,
					deadline, costo, tipoCosto1, minUsuarios, maxUsuarios);
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

		// TODO borrar este método
		@DELETE
		public void cleanArticulo(){
			repo.borrarArticulos();
		}
}
