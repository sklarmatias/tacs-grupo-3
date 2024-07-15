package ar.edu.utn.frba.tacs.service;

import ar.edu.utn.frba.tacs.model.Notification;
import ar.edu.utn.frba.tacs.repository.MongoDBConnector;
import ar.edu.utn.frba.tacs.repository.notifications.MongoNotificationsRepository;
import ar.edu.utn.frba.tacs.repository.notifications.NotificationsRepository;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.*;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

public class NotificationService {

    private final NotificationsRepository notificationsRepository;

    public NotificationService(String url) {
        notificationsRepository = new MongoNotificationsRepository(url);
    }
    public void generateClosedArticleNotificationSubscriber(Notification notification){
        System.out.println("Generating ClosedArticleNotificationSubscriber");
        notification.setType("ClosedArticleNotification");
        notificationsRepository.save(notification);
    }
    public void generateClosedArticleNotificationOwner(Notification notification){
        System.out.println("Generating ClosedArticleNotificationOwner");
        notification.setType("OwnerClosedArticleNotification");
        notificationsRepository.save(notification);
    }
    public void generateSubscriptionNotificationSubscriber(Notification notification){
        System.out.println("Generating SubscriptionNotificationSubscriber");
        notification.setType("SubscriptionNotification");
        notificationsRepository.save(notification);
    }
    public void generateSubscriptionNotificationOwner(Notification notification){
        System.out.println("Generating SubscriptionNotificationOwner");
        notification.setType("OwnerSubscriptionNotification");
        notificationsRepository.save(notification);
    }

    public List<Notification> getPendingNotifications() {
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("notified", false);

        return notificationsRepository.findAllCondition(conditions);
    }



    public int markAsNotified(String id) {
        Notification notification;
        try {
            notification = notificationsRepository.find(id);
        } catch (Exception e) {
            System.err.println("An error occurred while marking the notification as notified: " + e.getMessage());
            e.printStackTrace();
            return 1;
        }
        try{
            if(notification.isNotified()){
                throw new IllegalArgumentException("Already notified");
            }
            notification.setNotified(true);
            notificationsRepository.update(notification.getId(),notification);
        } catch (Exception e) {
            System.err.println("An error occurred while marking the notification as notified: " + e.getMessage());
            e.printStackTrace();
            return 2;
        }
        return 0;
    }
    public void delete(String id){
        notificationsRepository.delete(id);
    }
    public List<Notification> getAllNotifications(){
        return notificationsRepository.findAll();
    }
}
