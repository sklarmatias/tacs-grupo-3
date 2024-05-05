package ar.edu.utn.frba.tacs.service;

import ar.edu.utn.frba.tacs.model.Annotation;
import ar.edu.utn.frba.tacs.model.Article;
import ar.edu.utn.frba.tacs.model.User;
import ar.edu.utn.frba.tacs.repository.articles.ArticlesRepository;
import ar.edu.utn.frba.tacs.repository.articles.impl.InMemoryArticlesRepository;
import ar.edu.utn.frba.tacs.repository.user.UsersRepository;
import ar.edu.utn.frba.tacs.repository.user.impl.InMemoryUsersRepository;
import java.util.List;

public class ArticleService {

    private final ArticlesRepository articlesRepository = new InMemoryArticlesRepository();
    private final UsersRepository usersRepository = new InMemoryUsersRepository();

    public List<Article> listArticles() {
        return articlesRepository.findAll();
    }

    public Article getArticle(Integer id) {
        return articlesRepository.find(id);
    }

    public void updateArticle(Integer id, Article article) {
        articlesRepository.update(id,article);
    }

    // returns created article ID
    public Integer saveArticle(Article article, User user){
        user.getPostedArticles().add(article);
        return articlesRepository.save(new Article(
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
    }

    public void signUpUser(Article article, User user) {
        article.signUpUser(user);
    }

    public void closeArticle(Article article) {
        //TODO validate user is owner
        article.close();
    }

    public List<Annotation> getUsersSignedUp(Article article) {
        return article.getAnnotations();
    }

    public void clearArticles(){
        articlesRepository.delete();
    }

}