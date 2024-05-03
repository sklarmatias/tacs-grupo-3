package ar.edu.utn.frba.tacs.controller;


import ar.edu.utn.frba.tacs.model.Article;
import ar.edu.utn.frba.tacs.repository.articles.ArticlesRepository;
import ar.edu.utn.frba.tacs.repository.articles.impl.InMemoryArticlesRepository;
import ar.edu.utn.frba.tacs.model.User;
import ar.edu.utn.frba.tacs.repository.user.UsersRepository;
import ar.edu.utn.frba.tacs.repository.user.impl.InMemoryUsersRepository;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/reports")
public class ReportsController {
	private final UsersRepository usersRepository = new InMemoryUsersRepository();
    private final ArticlesRepository articlesRepository = new InMemoryArticlesRepository();

    @GET
    @Path("/users")
    public int getUsersCount() {
        return usersRepository.findAll().size();
    }
    
    @GET
    @Path("/articles")
    public int countArticles() {
        return articlesRepository.findAll().size();
    }
    
    @GET
    @Path("/articles/success")
    public int countSuccessfulArticles() {
        return (int) articlesRepository.findAll().stream().filter(Article::wasClosedSuccessfully).count();
    }
    
    @GET
    @Path("/articles/failed")
    public int countFailedArticles() {
        return (int) articlesRepository.findAll().stream().filter(article -> !article.wasClosedSuccessfully()).count();
    }

    @GET
    @Path("/engaged_users")
    public int getEngagedUsers() {
        return (int) usersRepository.findAll().stream().filter(User::hasInteracted).count();
    }
}
