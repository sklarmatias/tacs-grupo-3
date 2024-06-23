package ar.edu.utn.frba.tacs.service;

import ar.edu.utn.frba.tacs.model.Annotation;
import ar.edu.utn.frba.tacs.model.Article;
import ar.edu.utn.frba.tacs.model.Notification;
import ar.edu.utn.frba.tacs.model.User;
import ar.edu.utn.frba.tacs.repository.articles.ArticlesRepository;
import ar.edu.utn.frba.tacs.repository.articles.impl.MongoArticlesRepository;

import java.util.*;
import java.util.stream.Collectors;

public class ArticleService {

    private final ArticlesRepository articlesRepository;
    private final NotificationService notificationService;
    public ArticleService(String url){
        articlesRepository = new MongoArticlesRepository(url);
        notificationService = new NotificationService(url);
    }


    public List<Article> listArticles() {
        return articlesRepository.findAll();
    }
    public List<Article> listOpenArticles() {
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("status", "OPEN");
        return articlesRepository.findAllCondition(conditions);
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

    public Annotation signUpUser(Article article, User user) {
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

        for (String subscriber : currentSubscribers){
            notificationService.generateSubscriptionNotificationSubscriber(new Notification("",
                    articleName,subscriber,
                    false,
                    new Date(),
                    currentSubscribers.size() +1,
                    article.getUsersMin(),
                    article.getUsersMax()));
            if(article.isClosed() && Objects.equals(article.getUsersMax(), article.getAnnotationsCounter())){
                notificationService.generateClosedArticleNotificationSubscriber(new Notification("",
                        articleName,subscriber,
                        false,
                        new Date(),
                        currentSubscribers.size(),
                        article.getUsersMin(),
                        article.getUsersMax()));
            }
        }
        if(article.isClosed() && Objects.equals(article.getUsersMax(), article.getAnnotationsCounter())){
            notificationService.generateClosedArticleNotificationSubscriber(new Notification("",
                    articleName,user.getId(),
                    false,
                    new Date(),
                    currentSubscribers.size(),
                    article.getUsersMin(),
                    article.getUsersMax()));
        }
        notificationService.generateSubscriptionNotificationOwner(new Notification("",
                articleName,articleOwner,
                false,
                new Date(),
                currentSubscribers.size() +1,
                article.getUsersMin(),
                article.getUsersMax()));
        if(article.isClosed() && Objects.equals(article.getUsersMax(), article.getAnnotationsCounter())){
            notificationService.generateClosedArticleNotificationOwner(new Notification("",
                    articleName,articleOwner,
                    false,
                    new Date(),
                    currentSubscribers.size(),
                    article.getUsersMin(),
                    article.getUsersMax()));
        }
        return annotation;
    }

    public void closeArticle(Article article){
        article.close();
        List<String> currentSubscribers = article.getAnnotations().stream().map(ann -> ann.getUser().getId()).toList();
        String articleOwner = article.getOwner();
        String articleName = article.getName();
        articlesRepository.update(article.getId(),article);
        for (String subscriber : currentSubscribers){
            notificationService.generateClosedArticleNotificationSubscriber(new Notification("",
                    articleName,subscriber,
                    false,
                    new Date(),
                    currentSubscribers.size(),
                    article.getUsersMin(),
                    article.getUsersMax()));
        }
        if(article.isExpired()){
            notificationService.generateClosedArticleNotificationOwner(new Notification("",
                    articleName,articleOwner,
                    false,
                    new Date(),
                    currentSubscribers.size(),
                    article.getUsersMin(),
                    article.getUsersMax()));
        }
    }


    public List<Annotation> getUsersSignedUp(String articleId) {
        Article article = getArticle(articleId);
        return article.getAnnotations();
    }
    public void delete(String id){
        articlesRepository.delete(id);
    }

}

