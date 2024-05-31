package ar.edu.utn.frba.tacs.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Getter
@Setter
@NoArgsConstructor
public class Notification {
    private String id;
    private String message;
    private boolean notified;
    private String user;

    public Notification(String id, String message, boolean notified, String user) {
        this.id = id;
        this.message = message;
        this.notified = notified;
        this.user = user;
    }

    public NotificationDTO convertToDTO() {
        return new NotificationDTO(this);
    }

    public static class NotificationDTO {
        private String id;
        private String message;
        private boolean notified;
        private String user;

        public NotificationDTO(Notification notification) {
            this.id = notification.getId();
            this.message = notification.getMessage();
            this.notified = notification.isNotified();
            this.user = notification.getUser();
        }
    }
}