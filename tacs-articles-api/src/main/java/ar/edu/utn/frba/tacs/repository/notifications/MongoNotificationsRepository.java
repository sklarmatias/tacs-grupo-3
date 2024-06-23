package ar.edu.utn.frba.tacs.repository.notifications;

import ar.edu.utn.frba.tacs.model.Article;
import ar.edu.utn.frba.tacs.model.Notification;
import ar.edu.utn.frba.tacs.model.User;
import ar.edu.utn.frba.tacs.repository.MongoDBConnector;
import ar.edu.utn.frba.tacs.repository.objectMappers.MongoArticleMapper;
import ar.edu.utn.frba.tacs.repository.objectMappers.MongoNotificationMapper;
import ar.edu.utn.frba.tacs.repository.objectMappers.MongoUserMapper;
import org.bson.Document;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MongoNotificationsRepository implements NotificationsRepository{
    private final MongoDBConnector dbConnector;
    public MongoNotificationsRepository(String url){
        dbConnector = new MongoDBConnector(url);
    }
    @Override
    public void save(Notification notification){
        Document document = MongoNotificationMapper.convertNotificationToDocument(notification);
        dbConnector.insert("notifications", document);
    }
    @Override
    public void delete(String id){
        dbConnector.deleteById("notifications",id);
    }
    @Override
    public List<Notification> findAll(){
        return dbConnector.selectAll("notifications").stream().map(MongoNotificationMapper::convertDocumentToNotification).toList();
    }
    @Override
    public Notification find(String id) {
        Document document = dbConnector.selectById("notifications", id);
        return MongoNotificationMapper.convertDocumentToNotification(document);
    }
    @Override
    public void update(String id, Notification notification) {
        dbConnector.update("notifications", id, MongoNotificationMapper.convertNotificationToDocument(notification));
    }
    @Override
    public List<Notification> findAllCondition(Map<String, Object> conditions) {
        List<Document> documents = dbConnector.selectByEqCondition("notifications",conditions);
        return documents.stream()
                .map(MongoNotificationMapper::convertDocumentToNotification)
                .collect(Collectors.toList());
    }
}
