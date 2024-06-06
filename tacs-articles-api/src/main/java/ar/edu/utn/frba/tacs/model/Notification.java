package ar.edu.utn.frba.tacs.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class Notification {
    private String id;
    private String type;
    private String articleName;
    private String subscriber;
    private boolean notified;
    private Date dateTime;

    public Notification(String id, String type, String articleName, String subscriber, boolean notified, Date dateTime) {
        this.id = id;
        this.type = type;
        this.articleName = articleName;
        this.subscriber = subscriber;
        this.notified = notified;
        this.dateTime = dateTime;
    }

    public NotificationDTO convertToDTO() {
        return new NotificationDTO(this);
    }

    public static class NotificationDTO {

        public String id;
        public String type;
        public String articleName;
        public String subscriber;
        public boolean notified;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        public Date dateTime;

        public NotificationDTO(Notification notification) {
            this.id = notification.getId();
            this.type = notification.getType();
            this.articleName = notification.getArticleName();
            this.subscriber = notification.getSubscriber();
            this.notified = notification.isNotified();
            this.dateTime = notification.getDateTime();
        }
    }
}
