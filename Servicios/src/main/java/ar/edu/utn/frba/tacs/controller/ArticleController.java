package ar.edu.utn.frba.tacs.controller;

import java.util.List;
import java.util.stream.Collectors;
import ar.edu.utn.frba.tacs.model.Annotation;
import ar.edu.utn.frba.tacs.model.Article;
import ar.edu.utn.frba.tacs.repository.articles.ArticlesRepository;
import ar.edu.utn.frba.tacs.repository.articles.impl.InMemoryArticlesRepository;
import ar.edu.utn.frba.tacs.model.User;
import ar.edu.utn.frba.tacs.repository.user.UsersRepository;
import ar.edu.utn.frba.tacs.repository.user.impl.InMemoryUsersRepository;
import ar.edu.utn.frba.tacs.service.ArticleService;
import ar.edu.utn.frba.tacs.service.UserService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;

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
	public List<Article.ArticleDTO> listUserArticles(@PathParam("id") Integer id) {
		return articleService.listUserArticles(id).stream().map(Article::convertToDTO).collect(Collectors.toList());
	}

	@GET
	@Path("/{id}")
	@Produces("application/json")
	public Article.ArticleDTO getArticle(@PathParam("id") Integer id) {
		return articleService.getArticle(id).convertToDTO();
	}

	@PATCH
	@Path("/{id}")
	@Consumes("application/json")
	public void updateArticle(@PathParam("id") Integer id, Article article) {
		articleService.updateArticle(id,article);
	}


	// status 201
	// Location header -> get URL
	@POST
	@Consumes("application/json")
	public Response saveArticle(Article article, @Context UriInfo uriInfo){
		User user = userService.getUser(article.getOwner());
		int articleId = articleService.saveArticle(article,user);
		// get Location URI
		UriBuilder articleURIBuilder = uriInfo.getAbsolutePathBuilder();
		articleURIBuilder.path(Integer.toString(articleId));
		return Response.created(articleURIBuilder.build()).build();
	}

	// 204 NoContent
	@POST
	@Path("/{articleId}/users/{userId}")
	@Consumes("application/json")
	public void signUpUser(@PathParam("articleId") int articleId,
						   @PathParam("userId") int userId) {
		Article article = articleService.getArticle(articleId);
		User user = userService.getUser(userId);
		articleService.signUpUser(article,user);
	}

	// NoContent
	@PATCH
	@Path("/{articleId}/close")
	@Produces("application/json")
	public Article.ArticleDTO closeArticle(@PathParam("articleId") int articleId) {
		//TODO validate user is owner
		Article article = articleService.getArticle(articleId);
		articleService.closeArticle(article);
		return article.convertToDTO();
	}

	@GET
	@Path("/{id}/users")
	@Produces("application/json")
	public List<Annotation.AnnotationDTO> getUsersSignedUp(@PathParam("id") int id) {
		Article article = articleService.getArticle(id);
		return articleService.getUsersSignedUp(article).stream().map(Annotation::convertToDTO).collect(Collectors.toList());
	}

	@DELETE
	public void deleteArticles(){
		articleService.clearArticles();
	}
}
