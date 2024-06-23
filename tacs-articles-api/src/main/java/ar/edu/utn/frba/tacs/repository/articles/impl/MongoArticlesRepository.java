package ar.edu.utn.frba.tacs.repository.articles.impl;

import ar.edu.utn.frba.tacs.model.*;
import ar.edu.utn.frba.tacs.repository.MongoDBConnector;
import ar.edu.utn.frba.tacs.repository.articles.ArticlesRepository;
import ar.edu.utn.frba.tacs.repository.objectMappers.MongoAnnotationMapper;
import ar.edu.utn.frba.tacs.repository.objectMappers.MongoArticleMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class MongoArticlesRepository implements ArticlesRepository {
    private final MongoDBConnector dbConnector;
    public MongoArticlesRepository(String url){
        dbConnector = new MongoDBConnector(url);
    }

    @Override
    public List<Article> findAll() {
        List<Document> documents = dbConnector.selectAll("articles");
        return documents.stream()
                .map(MongoArticleMapper::convertDocumentToArticle)
                .collect(Collectors.toList());
    }
    @Override
    public List<Article> findAllCondition(Map<String, Object> conditions) {
        List<Document> documents = dbConnector.selectByEqCondition("articles",conditions);
        return documents.stream()
                .map(MongoArticleMapper::convertDocumentToArticle)
                .collect(Collectors.toList());
    }

    private Date yesterday(){
        return new Date(new Date().getTime() - 86400);
    }

    @Override
    public List<Article> findAllExpiredAndOpen() {
        List<Bson> conditions = new ArrayList<>();
        conditions.add(Filters.lt("deadline", yesterday()));
        List<Document> documents = dbConnector.selectByCondition("articles",conditions);
        return documents.stream()
                .map(MongoArticleMapper::convertDocumentToArticle)
                .collect(Collectors.toList());
    }

    @Override
    public Article find(String id) {
        Document doc = dbConnector.selectById("articles", id);
        if(doc != null){
            return MongoArticleMapper.convertDocumentToArticle(doc);
        }
        else{
            return null;
        }
    }

    @Override
    public List<Article> filter(String userid) {
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("owner", userid);
        List<Document> documents = dbConnector.selectByEqCondition("articles", conditions);
        return documents.stream()
                .map(MongoArticleMapper::convertDocumentToArticle)
                .collect(Collectors.toList());
    }

    @Override
    public String save(Article article) {
        Document doc = MongoArticleMapper.convertArticleToDocument(article);
        return dbConnector.insert("articles", doc);
    }

    @Override
    public void update(String id, Article article) {
        dbConnector.update("articles", id, MongoArticleMapper.convertArticleToDocument(article));
    }
    @Override
    public void updateAddAnnotation(String id, Annotation annotation){
        dbConnector.updateInsertInArray("articles",id,"annotations", MongoAnnotationMapper.convertAnnotationToDocument(annotation));
    }

    @Override
    public void delete(String id) {
        dbConnector.deleteById("articles",id);
    }


}
