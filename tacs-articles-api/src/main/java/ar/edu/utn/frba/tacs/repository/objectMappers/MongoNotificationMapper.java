package ar.edu.utn.frba.tacs.repository.objectMappers;

import ar.edu.utn.frba.tacs.model.Notification;
import org.bson.Document;

import java.util.Date;

public class MongoNotificationMapper {
    public static Notification convertDocumentToNotification(Document doc) {
        String id = doc.getObjectId("_id").toString();
        String type = doc.getString("type");
        String articleName = doc.getString("articleName");
        String subscriber = doc.getString("subscriber");
        boolean notified = doc.getBoolean("notified");
        Date date = doc.getDate("dateTime");
        int currentSubscribers = doc.getInteger("currentSubscribers", 0);
        int minSubscribers = doc.getInteger("minSubscribers", 0);
        int maxSubscribers = doc.getInteger("maxSubscribers", 0);
        Notification notification = new Notification(type, articleName, subscriber, notified, date, currentSubscribers, minSubscribers, maxSubscribers);
        notification.setId(id);
        return notification;
    }
    public static Document convertNotificationToDocument(Notification notification){
        return new Document()
                .append("type", notification.getType())
                .append("articleName", notification.getArticleName())
                .append("subscriber", notification.getSubscriber())
                .append("notified", notification.isNotified())
                .append("dateTime", notification.getDateTime())
                .append("currentSubscribers", notification.getCurrentSubscribers())
                .append("minSubscribers", notification.getMinSubscribers())
                .append("maxSubscribers", notification.getMaxSubscribers());
    }
}
