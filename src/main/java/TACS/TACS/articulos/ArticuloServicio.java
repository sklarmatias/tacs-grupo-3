package TACS.TACS.articulos;

import java.util.List;
import java.util.stream.Collectors;
import TACS.TACS.anotaciones.Anotacion;
import TACS.TACS.anotaciones.repositorio.RepositorioDeAnotaciones;
import TACS.TACS.anotaciones.repositorio.RepositorioDeAnotacionesEnMemoria;
import TACS.TACS.articulos.repositorios.RepositorioDeArticulos;
import TACS.TACS.articulos.repositorios.RepositorioDeArticulosEnMemoria;
import TACS.TACS.usuarios.Usuario;
import TACS.TACS.usuarios.repositorios.RepositorioDeUsuarios;
import TACS.TACS.usuarios.repositorios.RepositorioDeUsuariosEnMemoria;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;

@Path("/articulos")
public class ArticuloServicio {
	private RepositorioDeArticulos repo = new RepositorioDeArticulosEnMemoria();
	private RepositorioDeUsuarios repoUsuarios = new RepositorioDeUsuariosEnMemoria();

	private RepositorioDeAnotaciones repositorioDeAnotaciones = new RepositorioDeAnotacionesEnMemoria();


	@GET
	@Produces("application/json")
	public List<Articulo.ArticuloDTO> getArticulos() {
		return repo.listarArticulos().stream().map(Articulo::convertirADTO).collect(Collectors.toList());
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


	// devuelve un 201
	// en el Header, devuelve un header Location que contiene la URL de obtención del recurso
	@POST
	@Consumes("application/json")
	public Response addArticulo(Articulo articulo, @Context UriInfo uriInfo){
		Usuario usuario = repoUsuarios.obtenerUsuario(articulo.getPropietario().getId());
		usuario.getArticulosPublicados().add(articulo);
		int idArticulo = repo.guardarArticulo(new Articulo(
				articulo.getNombre(),
				articulo.getImagen(),
				articulo.getLink(),
				articulo.getUsuarioRecibe(),
				repoUsuarios.obtenerUsuario(articulo.getPropietario().getId()),
				articulo.getDeadline(),
				articulo.getCosto(),
				articulo.getTipoCosto(),
				articulo.getMinimoUsuarios(),
				articulo.getMaximoUsuarios())
		);
		// creo la URI con la que se obtendría el recurso creado
		UriBuilder articuloURIBuilder = uriInfo.getAbsolutePathBuilder();
		articuloURIBuilder.path(Integer.toString(idArticulo));
		return Response.created(articuloURIBuilder.build()).build();
	}

	// devuelve un NoContent
	@POST
	@Path("/{idArticulo}/usuarios/{idUsuario}")
	@Consumes("application/json")
	public void addUsuarioEnArticulo(@PathParam("idArticulo") int idAtriculo,
									 @PathParam("idUsuario") int idUsuario,
									 @Context UriInfo uriInfo) {
		Articulo articulo = repo.obtenerArticulo(idAtriculo);
		Usuario usuario = repoUsuarios.obtenerUsuario(idUsuario);
		articulo.anotarUsuario(usuario);
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
