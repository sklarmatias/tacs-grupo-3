package ar.edu.utn.frba.tacs.repository.user.impl;

import ar.edu.utn.frba.tacs.model.Annotation;
import ar.edu.utn.frba.tacs.model.Article;
import ar.edu.utn.frba.tacs.model.User;
import ar.edu.utn.frba.tacs.repository.MongoDBConnector;
import ar.edu.utn.frba.tacs.repository.objectMappers.MongoAnnotationMapper;
import ar.edu.utn.frba.tacs.repository.objectMappers.MongoArticleMapper;
import ar.edu.utn.frba.tacs.repository.objectMappers.MongoUserMapper;
import ar.edu.utn.frba.tacs.repository.user.UsersRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import org.bson.Document;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MongoUsersRepository implements UsersRepository {

    private final MongoDBConnector dbConnector;
    public MongoUsersRepository(String url){
        dbConnector = new MongoDBConnector(url);
    }

    @Override
    public List<User> findAll() {
        List<Document> documents = dbConnector.selectAll("users");
        return documents.stream().map(MongoUserMapper::convertDocumentToUser).toList();
    }

    @Override
    public User find(String id) {
        Document document = dbConnector.selectById("users", id);
        return MongoUserMapper.convertDocumentToUser(document);
    }

    @Override
    public User find(String email, String pass) {
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("email", email);
        conditions.put("pass", pass);
        Document document = dbConnector.selectByCondition("users", conditions).get(0);
        return MongoUserMapper.convertDocumentToUser(document);
    }

    @Override
    public void update(String id, User user) {
        dbConnector.update("users", id, MongoUserMapper.convertUserToDocument(user));
    }

    @Override
    public void delete(String id) {
        dbConnector.deleteById("users",id);
    }

    @Override
    public void updateAddArticle(String id, Article article){
        dbConnector.updateInsertInArray("users",id,"postedArticles", MongoArticleMapper.convertArticleToDocument(article));
    }
    @Override
    public void updateAddAnnotation(String id, Annotation annotation){
        dbConnector.updateInsertInArray("users",id,"annotations", MongoAnnotationMapper.convertAnnotationToDocument(annotation));
    }

    @Override
    public boolean userExists(String email) {
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("email", email);
        return !dbConnector.selectByCondition("users", conditions).isEmpty();
    }

    @Override
    public String save(User user) {
        Document document = MongoUserMapper.convertUserToDocument(user);
        return dbConnector.insert("users", document);
    }


}
