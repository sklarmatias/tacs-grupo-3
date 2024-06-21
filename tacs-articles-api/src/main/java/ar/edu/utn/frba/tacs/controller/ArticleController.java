package ar.edu.utn.frba.tacs.controller;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import ar.edu.utn.frba.tacs.model.Annotation;
import ar.edu.utn.frba.tacs.model.Article;
import ar.edu.utn.frba.tacs.model.ArticleStatus;
import ar.edu.utn.frba.tacs.model.User;
import ar.edu.utn.frba.tacs.service.ArticleService;
import ar.edu.utn.frba.tacs.service.UserService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

@Path("/articles")
public class ArticleController {

	private final ArticleService articleService;
	private final UserService userService;
	public ArticleController(){
		articleService= new ArticleService(System.getenv("CON_STRING"));
		userService= new UserService(System.getenv("CON_STRING"));
	}
	public ArticleController(ArticleService articleService, UserService userService){
		this.articleService = articleService;
		this.userService = userService;
	}


	@GET
	@Produces("application/json")
	public List<Article.ArticleDTO> listArticles(@HeaderParam("user") String userId) {
		if(userId == null){
			return articleService.listOpenArticles().stream().map(Article::convertToDTO).collect(Collectors.toList());
		}
		else {
			return articleService.listUserArticles(userId).stream().map(Article::convertToDTO).collect(Collectors.toList());
		}

	}

	@GET
	@Path("/{id}")
	@Produces("application/json")
	public Article.ArticleDTO getArticle(@PathParam("id") String id) {
		return articleService.getArticle(id).convertToDTO();
	}

	@PATCH
	@Path("/{id}")
	@Consumes("application/json")
	public Response updateArticle(@HeaderParam("user") String userId, @PathParam("id") String id, Article article) {
		if(userId == null){
			return Response.status(Response.Status.FORBIDDEN).build();
		}
		if(!Objects.equals(articleService.getArticle(id).getOwner(), userId)){
			return Response.status(Response.Status.FORBIDDEN).build();
		}
		else{
			articleService.updateArticle(id,article);
			return Response.ok().build();
		}
	}


	// status 201
	// Location header -> get URL
	@POST
	@Consumes("application/json")
	public Response saveArticle(@HeaderParam("user") String userId, Article article){
		if(userId == null){
			return Response.status(Response.Status.FORBIDDEN).build();
		}
		User user = userService.getUser(userId);
		if(user == null){
			return Response.status(Response.Status.BAD_REQUEST).entity("No existe el usuario").type( MediaType.TEXT_PLAIN).build();
		}
		Article completeNewArticle = new Article(
				article.getName(),
				article.getImage(),
				article.getLink(),
				article.getUserGets(),
				userId,
				article.getDeadline(),
				article.getCost(),
				article.getCostType(),
				article.getUsersMin(),
				article.getUsersMax()
		);
		String articleId = articleService.saveArticle(completeNewArticle);
		completeNewArticle.setId(articleId);
		userService.updateUserAddArticle(user.getId(),completeNewArticle);
		// get Location URI
//		UriBuilder articleURIBuilder = uriInfo.getAbsolutePathBuilder();
//		articleURIBuilder.path(articleId);
//		return Response.created(articleURIBuilder.build()).build();
		return Response.status(201).build();
	}

	// 204 NoContent
	@POST
	@Path("/{articleId}/users/")
	@Consumes("application/json")
	public Response signUpUser(@PathParam("articleId") String articleId,
						   @HeaderParam("user") String userId) {

		if(userId == null){
			return Response.status(Response.Status.FORBIDDEN).build();
		}
		System.out.println("Se recibio una solicitud de suscripcion: IdUsuario: " + userId + "\n IdArticulo: " + articleId);
		Article article = articleService.getArticle(articleId);
		if (article == null){
			System.out.println("Articulo no encontrado.");
			return Response.status(Response.Status.BAD_REQUEST).entity("Articulo no encontrado.").type( MediaType.TEXT_PLAIN).build();
		}

		User user = userService.getUser(userId);
		if (user == null){
			System.out.println("Usuario no encontrado.");
			return Response.status(Response.Status.BAD_REQUEST).entity("Usuario no encontrado.").type( MediaType.TEXT_PLAIN).build();
		}

		System.out.println("Se intenta suscribir al usuario...");
		try {
			Annotation annotation = articleService.signUpUser(article, user);
			userService.updateAddAnnotation(user.getId(),annotation);
		}
		catch (Exception ex){
			System.out.println(ex.getMessage());
			return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).type( MediaType.TEXT_PLAIN).build();
		}
		return Response.ok().build();
	}

	// NoContent
	@PATCH
	@Path("/{articleId}/close")
	@Produces("application/json")
	public Response closeArticle(@PathParam("articleId") String articleId,@HeaderParam("user") String userId) {
		if(userId == null){
			return Response.status(Response.Status.FORBIDDEN).build();
		}
		if(!Objects.equals(articleService.getArticle(articleId).getOwner(), userId)){
			return Response.status(Response.Status.FORBIDDEN).build();
		}
		else{
			try {
				articleService.closeArticle(articleService.getArticle(articleId));
				return Response.ok().build();
			}
			catch(Exception exception){
				return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).type( MediaType.TEXT_PLAIN).build();
			}
		}
	}

	@GET
	@Path("/{id}/users")
	@Produces("application/json")
	public Response getUsersSignedUp(@PathParam("id") String id) {
		Article article = articleService.getArticle(id);
		if (article == null)
			return Response.status(Response.Status.BAD_REQUEST).build();
		return Response.ok(articleService.getUsersSignedUp(id)).build();
	}

}
