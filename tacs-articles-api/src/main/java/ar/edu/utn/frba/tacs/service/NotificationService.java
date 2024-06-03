package ar.edu.utn.frba.tacs.service;

import ar.edu.utn.frba.tacs.model.Notification;
import ar.edu.utn.frba.tacs.model.User;
import ar.edu.utn.frba.tacs.repository.MongoDBConnector;
import org.bson.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationService {

    private final MongoDBConnector mongoDBConnector;
    private final String collectionName = "notifications";

    public NotificationService() {
        mongoDBConnector = new MongoDBConnector();
    }
    public NotificationService(String url) {
        mongoDBConnector = new MongoDBConnector(url);
    }

    public void generateClosedArticleNotification(String articleName, String articleOwner, List<String> currentSubscribers) {
        for (String subscriber : currentSubscribers) {
            Document notification = new Document()
                    .append("type", "ClosedArticleNotification")
                    .append("articleName", articleName)
                    .append("subscriber", subscriber)
                    .append("notified", false)
                    .append("dateTime", LocalDateTime.now());

            mongoDBConnector.insert(collectionName, notification);
        }

        // Notification for the article owner
        Document ownerNotification = new Document()
                .append("type", "OwnerClosedArticleNotification")
                .append("articleName", articleName)
                .append("subscriber", articleOwner)
                .append("notified", false)
                .append("dateTime", LocalDateTime.now());

        mongoDBConnector.insert(collectionName, ownerNotification);
    }

    public void generateSubscriptionNotification(String articleName, String articleOwner, List<String> currentSubscribers) {
        for (String subscriber : currentSubscribers) {
            Document notification = new Document()
                    .append("type", "SubscriptionNotification")
                    .append("articleName", articleName)
                    .append("subscriber", subscriber)
                    .append("notified", false)
                    .append("dateTime", LocalDateTime.now());

            mongoDBConnector.insert(collectionName, notification);
        }

        // Notification for the article owner
        Document ownerNotification = new Document()
                .append("type", "OwnerSubscriptionNotification")
                .append("articleName", articleName)
                .append("subscriber", articleOwner)
                .append("notified", false)
                .append("dateTime", LocalDateTime.now());

        mongoDBConnector.insert(collectionName, ownerNotification);
    }

    public List<Notification> getPendingNotifications() {
        List<Notification> pendingNotifications = new ArrayList<>();


        Map<String, Object> conditions = new HashMap<>();
        conditions.put("notified", false);


        List<Document> documents = mongoDBConnector.selectByCondition(collectionName, conditions);

        for (Document doc : documents) {
            Notification notification = documentToNotification(doc);
            pendingNotifications.add(notification);
        }

        return pendingNotifications;
    }

    private Notification documentToNotification(Document doc) {
        String id = doc.getObjectId("_id").toString();
        String message = doc.getString("message");
        boolean notified = doc.getBoolean("notified");
        String userId = doc.getString("user_id");

        return new Notification(id, message, notified, userId);
    }
}

