package ar.edu.utn.frba.tacs.service;

import ar.edu.utn.frba.tacs.model.Notification;
import ar.edu.utn.frba.tacs.repository.MongoDBConnector;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.*;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

public class NotificationService {

    private final MongoDBConnector mongoDBConnector;
    private final MongoCollection<Document> collection;

    public NotificationService(String url) {
        mongoDBConnector = new MongoDBConnector(url);
        collection = mongoDBConnector.getCollection("notifications");
    }

    public void generateClosedArticleNotification(String articleName, String articleOwner, List<String> currentSubscribers) {
        for (String subscriber : currentSubscribers) {
            Document notification = new Document()
                    .append("type", "ClosedArticleNotification")
                    .append("articleName", articleName)
                    .append("subscriber", subscriber)
                    .append("notified", false)
                    .append("dateTime", new Date());

            mongoDBConnector.insert("notifications", notification);
        }

        Document ownerNotification = new Document()
                .append("type", "OwnerClosedArticleNotification")
                .append("articleName", articleName)
                .append("subscriber", articleOwner)
                .append("notified", false)
                .append("dateTime", new Date());

        mongoDBConnector.insert("notifications", ownerNotification);
    }

    public void generateSubscriptionNotification(String articleName, String articleOwner, List<String> currentSubscribers) {
        for (String subscriber : currentSubscribers) {
            Document notification = new Document()
                    .append("type", "SubscriptionNotification")
                    .append("articleName", articleName)
                    .append("subscriber", subscriber)
                    .append("notified", false)
                    .append("dateTime", new Date());

            mongoDBConnector.insert("notifications", notification);
        }

        Document ownerNotification = new Document()
                .append("type", "OwnerSubscriptionNotification")
                .append("articleName", articleName)
                .append("subscriber", articleOwner)
                .append("notified", false)
                .append("dateTime", new Date());

        mongoDBConnector.insert("notifications", ownerNotification);
    }

    public List<Notification> getPendingNotifications() {
        List<Notification> pendingNotifications = new ArrayList<>();

        Map<String, Object> conditions = new HashMap<>();
        conditions.put("notified", false);

        List<Document> documents = mongoDBConnector.selectByCondition("notifications", conditions);

        for (Document doc : documents) {
            Notification notification = documentToNotification(doc);
            pendingNotifications.add(notification);
        }

        return pendingNotifications;
    }

    private Notification documentToNotification(Document doc) {
        String id = doc.getObjectId("_id").toString();
        String type = doc.getString("type");
        String articleName = doc.getString("articleName");
        String subscriber = doc.getString("subscriber");
        boolean notified = doc.getBoolean("notified");
        Date date = doc.getDate("dateTime");

        return new Notification(id, type, articleName, subscriber, notified, date);
    }

    public boolean markAsNotified(String id) {
        try {
            Document updatedDocument = collection.findOneAndUpdate(
                    eq("_id", new ObjectId(id)),
                    set("notified", true)
            );
            return updatedDocument != null;
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid ID format: " + e.getMessage());
            return false;
        } catch (NullPointerException e) {
            System.err.println("Collection is not initialized: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("An error occurred while marking the notification as notified: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
