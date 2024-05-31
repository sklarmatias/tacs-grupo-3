package ar.edu.utn.frba.tacs.service;

import ar.edu.utn.frba.tacs.model.Annotation;
import ar.edu.utn.frba.tacs.model.Article;
import ar.edu.utn.frba.tacs.model.User;
import ar.edu.utn.frba.tacs.repository.articles.ArticlesRepository;
import ar.edu.utn.frba.tacs.repository.articles.impl.MongoArticlesRepository;
import java.util.List;
import java.util.stream.Collectors;

public class ArticleService {

    private final ArticlesRepository articlesRepository = new MongoArticlesRepository();
    private final NotificationService notificationService = new NotificationService();
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
        System.out.println("Obteniendo Annotations...");
        List<String> currentSubscribers = article.getAnnotations().stream().map(ann -> ann.getUser().getId()).toList();
        System.out.println("Obteniendo article owner...");
        String articleOwner = article.getOwner();
        System.out.println("Obteniendo article name...");
        String articleName = article.getName();
        System.out.println("Realizando subscripcion en memoria...");
        Annotation annotation = article.signUpUser(user);
        System.out.println("Cambiando Annotation...");
        articlesRepository.updateAddAnnotation(article.getId(),annotation);
        System.out.println("Cambiando Articulo...");
        articlesRepository.update(article.getId(),article);

        notificationService.generateSubscriptionNotification (articleName, articleOwner, currentSubscribers);
    }

    public Article closeArticle(Article article){
        article.close();
        List<String> currentSubscribers = article.getAnnotations().stream().map(ann -> ann.getUser().getId()).toList();
        String articleOwner = article.getOwner();
        String articleName = article.getName();
        articlesRepository.update(article.getId(),article);
        notificationService.generateClosedArticleNotification (articleName, articleOwner, currentSubscribers);
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
