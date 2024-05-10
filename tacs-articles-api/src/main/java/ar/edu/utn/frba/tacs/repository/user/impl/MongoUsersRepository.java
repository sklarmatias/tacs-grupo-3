package ar.edu.utn.frba.tacs.repository.user.impl;

import ar.edu.utn.frba.tacs.model.User;
import ar.edu.utn.frba.tacs.repository.MongoDBConnector;
import ar.edu.utn.frba.tacs.repository.objectMappers.MongoUserMapper;
import ar.edu.utn.frba.tacs.repository.user.UsersRepository;
import org.bson.Document;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MongoUsersRepository implements UsersRepository {

    private final MongoDBConnector dbConnector = new MongoDBConnector();

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
        Document document = MongoUserMapper.convertUserToDocument(user);
        dbConnector.update("users", id, "", document);
    }

    @Override
    public String save(User user) {
        Document document = MongoUserMapper.convertUserToDocument(user);
        return dbConnector.insert("users", document);
    }

    @Override
    public void delete() {

    }

}
