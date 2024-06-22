package ar.edu.utn.frba.tacs.repository.notifications;

import ar.edu.utn.frba.tacs.model.Article;
import ar.edu.utn.frba.tacs.model.Notification;

import java.util.List;
import java.util.Map;

public interface NotificationsRepository {
    public void save(Notification notification);
    public void delete(String id);
    public List<Notification> findAll();
    public Notification find(String id);
    public void update(String id, Notification notification);
    public List<Notification> findAllCondition(Map<String, Object> conditions);
}
