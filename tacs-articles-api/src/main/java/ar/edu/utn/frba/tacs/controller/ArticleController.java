package ar.edu.utn.frba.tacs.controller;

import java.util.List;
import java.util.stream.Collectors;
import ar.edu.utn.frba.tacs.model.Annotation;
import ar.edu.utn.frba.tacs.model.Article;
import ar.edu.utn.frba.tacs.model.User;
import ar.edu.utn.frba.tacs.service.ArticleService;
import ar.edu.utn.frba.tacs.service.UserService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

@Path("/articles")
public class ArticleController {

	private final ArticleService articleService = new ArticleService();

	private final UserService userService = new UserService();

	@GET
	@Produces("application/json")
	public List<Article.ArticleDTO> listArticles() {
		return articleService.listArticles().stream().map(Article::convertToDTO).collect(Collectors.toList());
	}
	@GET
	@Path("/user/{id}")
	@Produces("application/json")
	public List<Article.ArticleDTO> listUserArticles(@PathParam("id") String id) {
		return articleService.listUserArticles(id).stream().map(Article::convertToDTO).collect(Collectors.toList());
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
	public void updateArticle(@PathParam("id") String id, Article article) {
		articleService.updateArticle(id,article);
	}


	// status 201
	// Location header -> get URL
	@POST
	@Consumes("application/json")
	public Response saveArticle(Article article, @Context UriInfo uriInfo){
		User user = userService.getUser(article.getOwner());
		if(user == null){
			return Response
					.status(Response.Status.BAD_REQUEST)
					.entity("No existe el usuario")
					.type( MediaType.TEXT_PLAIN)
					.build();
		}
		String articleId = articleService.saveArticle(article);
		article.setId(articleId);
		userService.updateUserAddArticle(user.getId(),article);
		// get Location URI
		UriBuilder articleURIBuilder = uriInfo.getAbsolutePathBuilder();
		articleURIBuilder.path(articleId);
		return Response.created(articleURIBuilder.build()).build();
	}

	// 204 NoContent
	@POST
	@Path("/{articleId}/users/{userId}")
	@Consumes("application/json")
	public void signUpUser(@PathParam("articleId") String articleId,
						   @PathParam("userId") String userId) {

		articleService.signUpUser(articleService.getArticle(articleId),userService.getUser(userId).convertToDTO());
	}

	// NoContent
	@PATCH
	@Path("/{articleId}/close")
	@Produces("application/json")
	public Article.ArticleDTO closeArticle(@PathParam("articleId") String articleId) {
		//TODO validate user is owner

		return articleService.closeArticle(articleService.getArticle(articleId)).convertToDTO();
	}

	@GET
	@Path("/{id}/users")
	@Produces("application/json")
	public List<Annotation> getUsersSignedUp(@PathParam("id") String id) {

		return articleService.getUsersSignedUp(id);
	}

	@DELETE
	@Path("/{id}/")
	public void deleteArticle(@PathParam("id") String id){
		articleService.clearArticle(id);
	}
}
