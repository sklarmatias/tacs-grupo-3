package ar.edu.utn.frba.tacs.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ar.edu.utn.frba.tacs.model.Annotation;
import ar.edu.utn.frba.tacs.model.Article;
import ar.edu.utn.frba.tacs.model.CostType;
import ar.edu.utn.frba.tacs.repository.MongoDBConnector;
import ar.edu.utn.frba.tacs.model.User;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.bson.Document;

@Path("/articles")
public class ArticleController {

//	private final ArticleService articleService = new ArticleService();
//
//	private final UserService userService = new UserService();

	MongoDBConnector dbConnector = new MongoDBConnector();

	@GET
	@Produces("application/json")
	public List<Article.ArticleDTO> listArticles() {
//		return articleService.listArticles().stream().map(Article::convertToDTO).collect(Collectors.toList());
		return Article.getAllArticles();
	}
	@GET
	@Path("/user/{id}")
	@Produces("application/json")
	public List<Article.ArticleDTO> listUserArticles(@PathParam("id") String id) {
//		return articleService.listUserArticles(id).stream().map(Article::convertToDTO).collect(Collectors.toList());
		return Article.listUserArticles(id);
	}

	@GET
	@Path("/{id}")
	@Produces("application/json")
	public Article.ArticleDTO getArticle(@PathParam("id") String id) {
//		return articleService.getArticle(id).convertToDTO();
		return new Article(id).convertToDTO();
	}

	@PATCH
	@Path("/{id}")
	@Consumes("application/json")
	public void updateArticle(@PathParam("id") Integer id, Article article) {

//		articleService.updateArticle(id,article);
	}


	// status 201
	// Location header -> get URL
	@POST
	@Consumes("application/json")
	public Response saveArticle(Article article, @Context UriInfo uriInfo){
//		User user = userService.getUser(article.getOwner());
//		int articleId = articleService.saveArticle(article,user);
//		// get Location URI
//		UriBuilder articleURIBuilder = uriInfo.getAbsolutePathBuilder();
//		articleURIBuilder.path(Integer.toString(articleId));
//		return Response.created(articleURIBuilder.build()).build();
		String id = new Article(article.getName(),article.getImage(),article.getLink(),article.getUserGets(),
				article.getOwner(),article.getDeadline(), article.getCost(),article.getCostType(),article.getUsersMin(),
				article.getUsersMax()).getId();
		if(id!=null){
			return Response.ok().build();
		}
		else{
			return Response.serverError().build();
		}
	}

	// 204 NoContent
	@POST
	@Path("/{articleId}/users/{userId}")
	@Consumes("application/json")
	public Response signUpUser(@PathParam("articleId") String articleId,
						   @PathParam("userId") String userId) {
//		Article article = articleService.getArticle(articleId);
//		User user = userService.getUser(userId);
//		articleService.signUpUser(article,user);

		Article article = new Article(articleId);
		User user = new User(userId);
		article.signUpUser(user);
		return Response.ok().build();
	}

	// NoContent
	@PATCH
	@Path("/{articleId}/close")
	@Produces("application/json")
	public Response closeArticle(@PathParam("articleId") String articleId) {
		//TODO validate user is owner
//		Article article = articleService.getArticle(articleId);
//		articleService.closeArticle(article);
//		return article.convertToDTO();
		Article article = new Article(articleId);
		article.close();
		return Response.ok().build();
	}

	@GET
	@Path("/{id}/users")
	@Produces("application/json")
	public List<Annotation> getUsersSignedUp(@PathParam("id") String id) {
//		Article article = articleService.getArticle(id);
//		return articleService.getUsersSignedUp(article).stream().map(Annotation::convertToDTO).collect(Collectors.toList());
		Article article = new Article(id);
		return article.getAnnotations();
	}

	@DELETE
	public void deleteArticles(){

//		articleService.clearArticles();
	}
}
