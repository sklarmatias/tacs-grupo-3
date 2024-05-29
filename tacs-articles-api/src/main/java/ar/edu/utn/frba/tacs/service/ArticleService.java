package ar.edu.utn.frba.tacs.service;

import ar.edu.utn.frba.tacs.model.Annotation;
import ar.edu.utn.frba.tacs.model.Article;
import ar.edu.utn.frba.tacs.model.User;
import ar.edu.utn.frba.tacs.repository.articles.ArticlesRepository;
import ar.edu.utn.frba.tacs.repository.articles.impl.MongoArticlesRepository;
import java.util.List;

public class ArticleService {

    private final ArticlesRepository articlesRepository = new MongoArticlesRepository();
    public List<Article> listArticles() {
        return articlesRepository.findAll();
    }
    public List<Article> listUserArticles(String user) {
        return articlesRepository.filter(user);
    }

    public Article getArticle(String id) {
        return articlesRepository.find(id);
    }

    public void updateArticle(String id, Article article) {
        articlesRepository.update(id,article);
    }

    // returns created article ID
    public String saveArticle(Article article){

        return articlesRepository.save(article);
    }

    public void signUpUser(Article article, User user) {
        Annotation annotation = article.signUpUser(user);
        articlesRepository.updateAddAnnotation(article.getId(),annotation);
        articlesRepository.update(article.getId(),article);
    }

    public Article closeArticle(Article article){
        article.close();
        articlesRepository.update(article.getId(),article);
        return article;
    }


    public List<Annotation> getUsersSignedUp(String articleId) {
        Article article = getArticle(articleId);
        return article.getAnnotations();
    }

    public void clearArticle(String id){
        articlesRepository.delete(id);
    }

}
