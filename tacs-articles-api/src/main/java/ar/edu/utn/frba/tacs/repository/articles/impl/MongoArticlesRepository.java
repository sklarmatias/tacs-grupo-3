package ar.edu.utn.frba.tacs.repository.articles.impl;

import ar.edu.utn.frba.tacs.model.*;
import ar.edu.utn.frba.tacs.repository.MongoDBConnector;
import ar.edu.utn.frba.tacs.repository.articles.ArticlesRepository;
import ar.edu.utn.frba.tacs.repository.objectMappers.MongoArticleMapper;
import org.bson.Document;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MongoArticlesRepository implements ArticlesRepository {

    private final MongoDBConnector dbConnector = new MongoDBConnector();

    @Override
    public List<Article> findAll() {
        List<Document> documents = dbConnector.selectAll("articles");
        return documents.stream()
                .map(MongoArticleMapper::convertDocumentToArticle)
                .collect(Collectors.toList());
    }

    @Override
    public Article find(String id) {
        Document doc = dbConnector.selectById("articles", id);
        return doc != null ? MongoArticleMapper.convertDocumentToArticle(doc) : null;
    }

    @Override
    public List<Article> filter(String userid) {
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("owner", userid);
        List<Document> documents = dbConnector.selectByCondition("articles", conditions);
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
        Document doc = MongoArticleMapper.convertArticleToDocument(article);
        dbConnector.update("articles", id, "", doc);
    }

    @Override
    public void delete() {

    }

}
