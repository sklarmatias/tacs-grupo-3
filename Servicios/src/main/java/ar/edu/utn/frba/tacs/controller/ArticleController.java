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
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;

@Path("/articles")
public class ArticleController {
	private final ArticlesRepository articlesRepository = new InMemoryArticlesRepository();
	private final UsersRepository usersRepository = new InMemoryUsersRepository();

	@GET
	@Produces("application/json")
	public List<Article.ArticleDTO> listArticles() {
		return articlesRepository.findAll().stream().map(Article::convertToDTO).collect(Collectors.toList());
	}

	@GET
	@Path("/{id}")
	@Produces("application/json")
	public Article.ArticleDTO getArticle(@PathParam("id") Integer id) {
		return articlesRepository.find(id).convertToDTO();
	}

	@PATCH
	@Path("/{id}")
	@Consumes("application/json")
	public void updateArticle(@PathParam("id") Integer id, Article article) {
		articlesRepository.update(id,article);
	}


	// status 201
	// Location header -> get URL
	@POST
	@Consumes("application/json")
	public Response saveArticle(Article article, @Context UriInfo uriInfo){
		User user = usersRepository.find(article.getOwner());
		user.getPostedArticles().add(article);
		int articleId = articlesRepository.save(new Article(
				article.getName(),
				article.getImage(),
				article.getLink(),
				article.getUserGets(),
				article.getOwner(),
				article.getDeadline(),
				article.getCost(),
				article.getCostType(),
				article.getUsersMin(),
				article.getUsersMax())
		);
		// get URI
		UriBuilder articleURIBuilder = uriInfo.getAbsolutePathBuilder();
		articleURIBuilder.path(Integer.toString(articleId));
		return Response.created(articleURIBuilder.build()).build();
	}

	// NoContent
	@POST
	@Path("/{articleId}/users/{userId}")
	@Consumes("application/json")
	public void signUpUser(@PathParam("articleId") int articleId,
						   @PathParam("userId") int userId) {
		Article article = articlesRepository.find(articleId);
		User user = usersRepository.find(userId);
		article.signUpUser(user);
	}

	// NoContent
	@PATCH
	@Path("/{articleId}/close")
	@Produces("application/json")
	public Article.ArticleDTO closeArticle(@PathParam("articleId") int articleId) {
		//TODO validate user is owner
		Article article = articlesRepository.find(articleId);
		article.close();
		return article.convertToDTO();
	}

	@GET
	@Path("/{id}/users")
	@Produces("application/json")
	public List<Annotation.AnnotationDTO> getUsersSignedUp(@PathParam("id") int id) {
		Article article = articlesRepository.find(id);
		return article.getAnnotations().stream().map(Annotation::convertToDTO).collect(Collectors.toList());
	}

	@DELETE
	public void deleteArticles(){
		articlesRepository.delete();
	}
}
