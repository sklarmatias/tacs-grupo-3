package ar.edu.utn.frba.tacs.service;

import ar.edu.utn.frba.tacs.model.Article;
import ar.edu.utn.frba.tacs.model.User;
import ar.edu.utn.frba.tacs.repository.articles.ArticlesRepository;
import ar.edu.utn.frba.tacs.repository.articles.impl.InMemoryArticlesRepository;
import ar.edu.utn.frba.tacs.repository.user.UsersRepository;
import ar.edu.utn.frba.tacs.repository.user.impl.InMemoryUsersRepository;

public class ReportService {

    private final UsersRepository usersRepository = new InMemoryUsersRepository();
    private final ArticlesRepository articlesRepository = new InMemoryArticlesRepository();

    public int getUsersCount() {
        return usersRepository.findAll().size();
    }

    public int countArticles() {
        return articlesRepository.findAll().size();
    }

    public int countSuccessfulArticles() {
        return (int) articlesRepository.findAll().stream().filter(Article::wasClosedSuccessfully).count();
    }

    public int countFailedArticles() {
        return (int) articlesRepository.findAll().stream().filter(article -> !article.wasClosedSuccessfully()).count();
    }

    public int getEngagedUsers() {
        return (int) usersRepository.findAll().stream().filter(User::hasInteracted).count();
    }

}
