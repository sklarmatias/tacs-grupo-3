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

    // Nuevos campos
    private int currentSubscribers;
    private int minSubscribers;
    private int maxSubscribers;

    public Notification(String type, String articleName, String subscriber, boolean notified, Date dateTime,
                        int currentSubscribers, int minSubscribers, int maxSubscribers) {
        this.type = type;
        this.articleName = articleName;
        this.subscriber = subscriber;
        this.notified = notified;
        this.dateTime = dateTime;
        this.currentSubscribers = currentSubscribers;
        this.minSubscribers = minSubscribers;
        this.maxSubscribers = maxSubscribers;
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
        public int currentSubscribers;
        public int minSubscribers;
        public int maxSubscribers;

        public NotificationDTO(Notification notification) {
            this.id = notification.getId();
            this.type = notification.getType();
            this.articleName = notification.getArticleName();
            this.subscriber = notification.getSubscriber();
            this.notified = notification.isNotified();
            this.dateTime = notification.getDateTime();
            this.currentSubscribers = notification.getCurrentSubscribers();
            this.minSubscribers = notification.getMinSubscribers();
            this.maxSubscribers = notification.getMaxSubscribers();
        }
    }
}
